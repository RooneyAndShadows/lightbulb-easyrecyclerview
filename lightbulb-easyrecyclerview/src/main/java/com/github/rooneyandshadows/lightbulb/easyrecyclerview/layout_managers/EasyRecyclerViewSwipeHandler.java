package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;

import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView;
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.R;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyRecyclerAdapter;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class EasyRecyclerViewSwipeHandler<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends ItemTouchHelper.SimpleCallback {
    private Boolean undoClicked = false;
    private Snackbar snackbar;
    private Runnable pendingAction;
    private SwipeCallbacks<IType> swipeCallbacks;
    private final AType adapter;
    private final SwipeConfiguration configuration;
    private final SwipeToDeleteDrawerHelper drawer;
    private final EasyRecyclerView<IType, AType> easyRecyclerView;
    private final EasyRecyclerView<IType, AType> recyclerView;
    private final Handler actionsHandler = new Handler(Looper.getMainLooper(), null);
    private final boolean isVerticalLayoutManager;

    public void setSwipeCallbacks(SwipeCallbacks<IType> callbacks) {
        this.swipeCallbacks = callbacks;
    }

    public EasyRecyclerViewSwipeHandler(EasyRecyclerView<IType, AType> easyRecyclerView, AType adapter, SwipeConfiguration configuration) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.configuration = configuration;
        this.easyRecyclerView = easyRecyclerView;
        this.recyclerView = easyRecyclerView;
        this.adapter = adapter;
        this.drawer = new SwipeToDeleteDrawerHelper();
        this.isVerticalLayoutManager = recyclerView.getLayoutManager().canScrollVertically();
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        Directions dragDirections = Directions.UP_DOWN;
        Directions swipeDirections = Directions.LEFT_RIGHT;
        IType item = getItem(viewHolder);
        if (swipeCallbacks != null)
            swipeDirections = swipeCallbacks.setAllowedSwipeDirections(item);
        return ItemTouchHelper.Callback.makeMovementFlags(dragDirections.value, swipeDirections.value);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAbsoluteAdapterPosition() - adapter.getHeadersCount();
        int toPosition = target.getAbsoluteAdapterPosition() - adapter.getHeadersCount();
        adapter.moveItem(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        IType item = getItem(viewHolder);
        showSwipedItemSnackBar(item, Directions.valueOf(direction));
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (easyRecyclerView.supportsPullToRefresh())
            easyRecyclerView.enablePullToRefreshLayout(true);
        if (isItemVisible(viewHolder))
            executePendingAction();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        switch (actionState) {
            case ItemTouchHelper.ACTION_STATE_DRAG:
                break;
            case ItemTouchHelper.ACTION_STATE_SWIPE:
                handleSwipeDraw(viewHolder, c, dX, dY);
                break;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void handleSwipeDraw(RecyclerView.ViewHolder viewHolder, Canvas canvas, float dx, float dy) {
        View itemView = viewHolder.itemView;
        if (easyRecyclerView.supportsPullToRefresh())
            easyRecyclerView.enablePullToRefreshLayout(false);
        int itemPosition = viewHolder.getAbsoluteAdapterPosition() - adapter.getHeadersCount();
        if (itemPosition != -1) {
            IType item = getItem(viewHolder);
            int moved = -1;
            if (item != null) {
                Directions direction = null;
                if (isVerticalLayoutManager) {
                    if (dx > 0)
                        direction = Directions.RIGHT;
                    else if (dx < 0)
                        direction = Directions.LEFT;
                } else {
                    if (dy > 0)
                        direction = Directions.UP;
                    else if (dy < 0)
                        direction = Directions.DOWN;
                }
                if (direction == null) {
                    drawer.clearBounds();
                    return;
                }
                switch (direction) {
                    case LEFT:
                    case RIGHT:
                        moved = (int) dx;
                        break;
                    case UP:
                    case DOWN:
                        moved = (int) dy;
                        break;
                }
                String actionText = getActionText(item);
                int backgroundColor = getActionBackgroundColor(direction);
                Drawable icon = getActionIcon(direction);
                drawer.draw(actionText, itemView, moved, canvas, direction, backgroundColor, icon);
            }
        }
    }

    public void cancelPendingAction() {
        undoClicked = true;
        runPending();
    }

    public void executePendingAction() {
        runPending();
    }

    private void runPending() {
        if (pendingAction != null) {
            actionsHandler.removeCallbacks(pendingAction);
            snackbar.dismiss();
            pendingAction.run();
        }
    }

    private void addPendingAction(IType item, Directions direction) {
        undoClicked = false;
        pendingAction = () -> {
            int position = adapter.getPosition(item);
            if (swipeCallbacks != null) {
                if (undoClicked) {
                    recyclerView.itemChanged(position);
                    swipeCallbacks.onActionCancelled(item, position);
                } else {
                    swipeCallbacks.onSwipeActionApplied(item, position, direction);
                }
                pendingAction = null;
            }
        };
        actionsHandler.postDelayed(pendingAction, configuration.pendingActionDelay);
    }

    @SuppressWarnings("ShowToast")
    private synchronized void showSwipedItemSnackBar(IType item, Directions direction) {
        executePendingAction();
        addPendingAction(item, direction);
        String pendingActionText = "";
        String undoText = configuration.swipeSnackBarUndoTextPhrase;
        if (swipeCallbacks != null) {
            pendingActionText = swipeCallbacks.getPendingActionText(direction);
            undoText = swipeCallbacks.getCancelActionText();
        }
        snackbar = Snackbar.make(recyclerView, pendingActionText, configuration.pendingActionDelay)
                .setAction(undoText, view -> cancelPendingAction())
                .setActionTextColor(configuration.swipeNegativeBackgroundColor)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        View backgroundView = snackbar.getView();
        snackbar.show();
    }

    private Boolean isItemVisible(RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() != null)
            return recyclerView.getLayoutManager().isViewPartiallyVisible(viewHolder.itemView, false, true);
        return false;
    }

    private String getActionText(IType item) {
        String actionText = "";
        if (swipeCallbacks != null && item != null)
            actionText = swipeCallbacks.getActionBackgroundText(item);
        return actionText;
    }

    private int getActionBackgroundColor(Directions directions) {
        int background = ResourceUtils.getColorByAttribute(recyclerView.getContext(), R.attr.colorError);
        if (swipeCallbacks != null)
            background = swipeCallbacks.getSwipeBackgroundColor(directions);
        return background;
    }

    private Drawable getActionIcon(Directions directions) {
        Drawable icon = ResourceUtils.getDrawable(recyclerView.getContext(), R.drawable.icon_delete);
        if (swipeCallbacks != null)
            icon = swipeCallbacks.getSwipeIcon(directions);
        return icon;
    }

    private IType getItem(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAbsoluteAdapterPosition() - adapter.getHeadersCount();
        return adapter.getItem(position);
    }

    private class SwipeToDeleteDrawerHelper {
        private ColorDrawable backgroundDrawable;

        void clearBounds() {
            if (backgroundDrawable != null) {
                backgroundDrawable.setBounds(0, 0, 0, 0);
            }
        }

        void draw(String text, View itemView, int moved, Canvas canvas, Directions direction, int backgroundColor, Drawable icon) {
            backgroundDrawable = new ColorDrawable(backgroundColor);
            backgroundDrawable.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            backgroundDrawable.draw(canvas);
            canvas.save();
            int backgroundCornerOffset = 20;
            int iconMargin = (itemView.getHeight() - configuration.swipeIconSize) / 2;
            int iconTop = -1;
            int iconBottom = -1;
            int iconLeft = -1;
            int iconRight = -1;
            Paint textPaint = new Paint();
            textPaint.setColor(configuration.swipeAccentColor);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(configuration.swipeTextSize);
            textPaint.setElegantTextHeight(true);
            float yPos = calculateTextYpos(itemView, textPaint);
            float textWidth = textPaint.measureText(text);
            switch (direction) {
                case LEFT:
                    canvas.clipRect(itemView.getRight() + moved - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    iconTop = itemView.getTop() + (itemView.getHeight() - configuration.swipeIconSize) / 2;
                    iconBottom = iconTop + configuration.swipeIconSize;
                    iconLeft = itemView.getRight() - iconMargin - configuration.swipeIconSize;
                    iconRight = itemView.getRight() - iconMargin;
                    canvas.drawText(text, iconLeft - iconMargin - textWidth, yPos, textPaint);
                    break;
                case RIGHT:
                    canvas.clipRect(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + moved + backgroundCornerOffset, itemView.getBottom());
                    iconTop = itemView.getTop() + (itemView.getHeight() - configuration.swipeIconSize) / 2;
                    iconBottom = iconTop + configuration.swipeIconSize;
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = itemView.getLeft() + iconMargin + configuration.swipeIconSize;
                    canvas.drawText(text, iconRight + iconMargin, yPos, textPaint);
                    break;
                case UP:
                    canvas.clipRect(itemView.getLeft(), itemView.getTop() + moved - backgroundCornerOffset, itemView.getRight(), itemView.getBottom());
                    iconTop = itemView.getTop() + (itemView.getHeight() - configuration.swipeIconSize) / 2;
                    iconBottom = iconTop + configuration.swipeIconSize;
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = itemView.getLeft() + iconMargin + configuration.swipeIconSize;
                    canvas.drawText(text, iconRight + iconMargin, yPos, textPaint);
                    break;
                case DOWN:
                    canvas.clipRect(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom() + moved - backgroundCornerOffset);
                    iconTop = itemView.getTop() + (itemView.getHeight() - configuration.swipeIconSize) / 2;
                    iconBottom = iconTop + configuration.swipeIconSize;
                    iconLeft = itemView.getRight() - iconMargin - configuration.swipeIconSize;
                    iconRight = itemView.getRight() - iconMargin;
                    canvas.drawText(text, iconLeft - iconMargin - textWidth, yPos, textPaint);
                    break;
            }
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.draw(canvas);
            canvas.restore();
        }

        private float calculateTextYpos(View itemView, Paint paint) {
            int itemHeight = itemView.getHeight();
            int itemCenter = itemHeight / 2;
            float textHeight = ((paint.descent() + paint.ascent()) / 2);
            return itemView.getTop() + (itemCenter - textHeight);
        }
    }

    public interface SwipeCallbacks<ItemType extends EasyAdapterDataModel> {

        Directions setAllowedSwipeDirections(ItemType item);

        Directions setAllowedDragDirections(ItemType item);

        String getActionBackgroundText(ItemType item);

        void onSwipeActionApplied(ItemType item, int position, Directions direction);

        void onActionCancelled(ItemType item, Integer position);

        int getSwipeBackgroundColor(Directions direction);

        Drawable getSwipeIcon(Directions direction);

        String getPendingActionText(Directions direction);

        String getCancelActionText();
    }

    public enum Directions {
        UP(ItemTouchHelper.UP),
        DOWN(ItemTouchHelper.DOWN),
        LEFT(ItemTouchHelper.LEFT),
        RIGHT(ItemTouchHelper.RIGHT),
        LEFT_RIGHT(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT),
        UP_DOWN(ItemTouchHelper.UP | ItemTouchHelper.DOWN),
        ALL(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN);

        private final int value;
        private static final SparseArray<Directions> values = new SparseArray<>();

        Directions(int value) {
            this.value = value;
        }

        static {
            for (Directions editMode : Directions.values()) {
                values.put(editMode.value, editMode);
            }
        }

        public static Directions valueOf(int pageType) {
            return values.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    public static class SwipeConfiguration {
        private static final String UNDO_TEXT_TAG = "UNDO_TEXT_TAG";
        private static final String ICON_SIZE_TAG = "ICON_SIZE_TAG";
        private static final String TEXT_SIZE_TAG = "TEXT_SIZE_TAG";
        private static final String COLOR_ACCENT_TAG = "COLOR_ACCENT_TAG";
        private static final String COLOR_BG_POSITIVE_TAG = "COLOR_BG_POSITIVE_TAG";
        private static final String COLOR_BG_NEGATIVE_TAG = "COLOR_BG_NEGATIVE_TAG";
        private static final String COLOR_BG_SNACKBAR_TAG = "COLOR_BG_SNACKBAR_TAG";
        private static final String SNACKBAR_TEXT_COLOR_TAG = "SNACKBAR_TEXT_COLOR_TAG";
        private String swipeSnackBarUndoTextPhrase;
        private int swipeAccentColor;
        private int swipeIconSize;
        private int swipeTextSize;
        private int swipePositiveBackgroundColor;
        private int swipeNegativeBackgroundColor;
        private final int pendingActionDelay = 4000;

        public Bundle saveConfigurationState() {
            Bundle savedState = new Bundle();
            savedState.putString(UNDO_TEXT_TAG, swipeSnackBarUndoTextPhrase);
            savedState.putInt(ICON_SIZE_TAG, swipeIconSize);
            savedState.putInt(TEXT_SIZE_TAG, swipeTextSize);
            savedState.putInt(COLOR_ACCENT_TAG, swipeAccentColor);
            savedState.putInt(COLOR_BG_POSITIVE_TAG, swipePositiveBackgroundColor);
            savedState.putInt(COLOR_BG_NEGATIVE_TAG, swipeNegativeBackgroundColor);
            return savedState;
        }

        public void restoreConfigurationState(Bundle savedState) {
            swipeSnackBarUndoTextPhrase = savedState.getString(UNDO_TEXT_TAG);
            swipeIconSize = savedState.getInt(ICON_SIZE_TAG);
            swipeTextSize = savedState.getInt(TEXT_SIZE_TAG);
            swipeAccentColor = savedState.getInt(COLOR_ACCENT_TAG);
            swipePositiveBackgroundColor = savedState.getInt(COLOR_BG_POSITIVE_TAG);
            swipeNegativeBackgroundColor = savedState.getInt(COLOR_BG_NEGATIVE_TAG);
        }

        public void setSwipeSnackBarUndoTextPhrase(String swipeSnackBarUndoTextPhrase) {
            this.swipeSnackBarUndoTextPhrase = swipeSnackBarUndoTextPhrase;
        }

        public void setSwipeIconSize(int swipeIconSize) {
            this.swipeIconSize = swipeIconSize;
        }

        public void setSwipeTextSize(int swipeTextSize) {
            this.swipeTextSize = swipeTextSize;
        }

        public void setSwipeAccentColor(int swipeAccentColor) {
            this.swipeAccentColor = swipeAccentColor;
        }

        public void setSwipePositiveBackgroundColor(int swipePositiveBackgroundColor) {
            this.swipePositiveBackgroundColor = swipePositiveBackgroundColor;
        }

        public void setSwipeNegativeBackgroundColor(int swipeNegativeBackgroundColor) {
            this.swipeNegativeBackgroundColor = swipeNegativeBackgroundColor;
        }

        public String getSwipeSnackBarUndoTextPhrase() {
            return swipeSnackBarUndoTextPhrase;
        }

        public int getSwipeAccentColor() {
            return swipeAccentColor;
        }

        public int getSwipeIconSize() {
            return swipeIconSize;
        }

        public int getSwipeTextSize() {
            return swipeTextSize;
        }

        public int getSwipePositiveBackgroundColor() {
            return swipePositiveBackgroundColor;
        }

        public int getSwipeNegativeBackgroundColor() {
            return swipeNegativeBackgroundColor;
        }

        public int getPendingActionDelay() {
            return pendingActionDelay;
        }
    }
}