package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class EasyRecyclerViewTouchHandler<IType extends EasyAdapterDataModel, AType extends EasyRecyclerAdapter<IType>> extends ItemTouchHelper.SimpleCallback {
    private Boolean undoClicked = false;
    private Snackbar snackbar;
    private Runnable pendingAction;
    private final TouchCallbacks<IType> swipeCallbacks;
    private final AType adapter;
    private final SwipeConfiguration configuration;
    private final SwipeToDeleteDrawerHelper drawer;
    private final EasyRecyclerView<IType, AType> easyRecyclerView;
    private final Handler actionsHandler = new Handler(Looper.getMainLooper(), null);
    private final boolean isVerticalLayoutManager;
    private Directions allowedDragDirections;

    public EasyRecyclerViewTouchHandler(EasyRecyclerView<IType, AType> easyRecyclerView, TouchCallbacks<IType> swipeCallbacks) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.swipeCallbacks = swipeCallbacks;
        this.configuration = swipeCallbacks.getConfiguration(easyRecyclerView.getContext());
        this.easyRecyclerView = easyRecyclerView;
        this.adapter = easyRecyclerView.getAdapter();
        this.drawer = new SwipeToDeleteDrawerHelper();
        this.isVerticalLayoutManager = easyRecyclerView.getLayoutManager().canScrollVertically();
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAbsoluteAdapterPosition();
        if (adapter.getHeadersCount() > 0 && position < adapter.getHeadersCount())
            return 0;
        if (adapter.getFootersCount() > 0 && position >= adapter.getItemCount())
            return 0;
        Directions allowedSwipeDirections = Directions.NONE;
        allowedDragDirections = Directions.NONE;
        IType item = getItem(viewHolder);
        if (swipeCallbacks != null) {
            allowedSwipeDirections = swipeCallbacks.getAllowedSwipeDirections(item);
            allowedDragDirections = swipeCallbacks.getAllowedDragDirections(item);
        }
        return ItemTouchHelper.Callback.makeMovementFlags(allowedDragDirections.value, allowedSwipeDirections.value);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return allowedDragDirections != Directions.NONE;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAbsoluteAdapterPosition() - adapter.getHeadersCount();
        int toPosition = target.getAbsoluteAdapterPosition() - adapter.getHeadersCount();
        if (toPosition < 0 || toPosition > adapter.getItemCount())
            return false;
        adapter.moveItem(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        IType item = getItem(viewHolder);
        showSwipedItemSnackBar(item, Directions.valueOf(direction));
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null)
            viewHolder.itemView.setAlpha(0.6F);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(1F);
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
                    adapter.notifyItemChanged(position + adapter.getHeadersCount());
                    swipeCallbacks.onActionCancelled(item, adapter, position);
                } else {
                    swipeCallbacks.onSwipeActionApplied(item, position, adapter, direction);
                }
                pendingAction = null;
            }
        };
        actionsHandler.postDelayed(pendingAction, configuration.swipeActionDelay);
    }

    @SuppressWarnings("ShowToast")
    private synchronized void showSwipedItemSnackBar(IType item, Directions direction) {
        executePendingAction();
        addPendingAction(item, direction);
        String pendingActionText = "";
        String undoText = configuration.swipeCancelActionText;
        if (swipeCallbacks != null) {
            pendingActionText = swipeCallbacks.getPendingActionText(direction);
            undoText = configuration.getSwipeCancelActionText();
        }
        snackbar = Snackbar.make(easyRecyclerView, pendingActionText, configuration.swipeActionDelay)
                .setAction(undoText, view -> cancelPendingAction())
                .setActionTextColor(ResourceUtils.getColorByAttribute(easyRecyclerView.getContext(), R.attr.colorError))
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        View backgroundView = snackbar.getView();
        snackbar.show();
    }

    private Boolean isItemVisible(RecyclerView.ViewHolder viewHolder) {
        if (easyRecyclerView.getLayoutManager() != null)
            return easyRecyclerView.getLayoutManager().isViewPartiallyVisible(viewHolder.itemView, false, true);
        return false;
    }

    private String getActionText(IType item) {
        String actionText = "";
        if (swipeCallbacks != null && item != null)
            actionText = swipeCallbacks.getActionBackgroundText(item);
        return actionText;
    }

    private int getActionBackgroundColor(Directions directions) {
        int background = ResourceUtils.getColorByAttribute(easyRecyclerView.getContext(), R.attr.colorError);
        if (swipeCallbacks != null)
            background = swipeCallbacks.getSwipeBackgroundColor(directions);
        return background;
    }

    private Drawable getActionIcon(Directions directions) {
        Drawable icon = ResourceUtils.getDrawable(easyRecyclerView.getContext(), R.drawable.icon_delete);
        if (swipeCallbacks != null)
            icon = swipeCallbacks.getSwipeIcon(directions);
        icon.setTint(configuration.swipeTextAndIconColor);
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
            textPaint.setColor(configuration.swipeTextAndIconColor);
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

    public interface TouchCallbacks<ItemType extends EasyAdapterDataModel> {

        Directions getAllowedSwipeDirections(ItemType item);

        Directions getAllowedDragDirections(ItemType item);

        void onSwipeActionApplied(ItemType item, int position, EasyRecyclerAdapter<ItemType> adapter, Directions direction);

        void onActionCancelled(ItemType item, EasyRecyclerAdapter<ItemType> adapter, Integer position);

        String getActionBackgroundText(ItemType item);

        int getSwipeBackgroundColor(Directions direction);

        Drawable getSwipeIcon(Directions direction);

        String getPendingActionText(Directions direction);

        SwipeConfiguration getConfiguration(Context context);
    }

    public enum Directions {
        UP(ItemTouchHelper.UP),
        DOWN(ItemTouchHelper.DOWN),
        LEFT(ItemTouchHelper.LEFT),
        RIGHT(ItemTouchHelper.RIGHT),
        LEFT_RIGHT(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT),
        UP_DOWN(ItemTouchHelper.UP | ItemTouchHelper.DOWN),
        ALL(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN),
        NONE(0);

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
        private String swipeCancelActionText;
        private int swipeTextAndIconColor;
        private int swipeIconSize;
        private int swipeTextSize;
        private int swipeActionDelay;

        public SwipeConfiguration(Context context) {
            this.swipeCancelActionText = ResourceUtils.getPhrase(context, R.string.erv_swipe_undo_default_text);
            this.swipeTextAndIconColor = ResourceUtils.getColorById(context, R.color.white);
            this.swipeIconSize = ResourceUtils.getDimenPxById(context, R.dimen.erv_swipe_icon_size);
            this.swipeTextSize = ResourceUtils.getDimenPxById(context, R.dimen.erv_swipe_text_size);
            this.swipeActionDelay = 4000;
        }

        public SwipeConfiguration withSwipeCancelActionText(String text) {
            this.swipeCancelActionText = text;
            return this;
        }

        public SwipeConfiguration withSwipeTextAndIconColor(int color) {
            this.swipeTextAndIconColor = color;
            return this;
        }

        public SwipeConfiguration withSwipeIconSize(int color) {
            this.swipeIconSize = color;
            return this;
        }

        public SwipeConfiguration withSwipeTextSize(int color) {
            this.swipeTextSize = color;
            return this;
        }

        public SwipeConfiguration withSwipeActionDelay(int delay) {
            if (delay < 2000)
                delay = 2000;
            this.swipeActionDelay = delay;
            return this;
        }

        public String getSwipeCancelActionText() {
            return swipeCancelActionText;
        }

        public int getSwipeTextAndIconColor() {
            return swipeTextAndIconColor;
        }

        public int getSwipeIconSize() {
            return swipeIconSize;
        }

        public int getSwipeTextSize() {
            return swipeTextSize;
        }

        public int getSwipeActionDelay() {
            return swipeActionDelay;
        }
    }
}