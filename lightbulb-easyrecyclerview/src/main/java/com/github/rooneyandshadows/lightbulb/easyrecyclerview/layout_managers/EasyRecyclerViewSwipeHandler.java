package com.github.rooneyandshadows.lightbulb.easyrecyclerview.layout_managers;

import android.content.Context;
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
    private final RecyclerView recyclerView;
    private final Handler actionsHandler = new Handler(Looper.getMainLooper(), null);

    public void setSwipeCallbacks(SwipeCallbacks<IType> callbacks) {
        this.swipeCallbacks = callbacks;
    }

    public EasyRecyclerViewSwipeHandler(EasyRecyclerView<IType, AType> easyRecyclerView, AType adapter, SwipeConfiguration configuration) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.configuration = configuration;
        this.easyRecyclerView = easyRecyclerView;
        this.recyclerView = easyRecyclerView.getRecyclerView();
        this.adapter = adapter;
        this.drawer = new SwipeToDeleteDrawerHelper(recyclerView.getContext(), configuration.getEditMode());
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        IType item = getItem(viewHolder);
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        if (swipeCallbacks != null)
            swipeFlags = swipeCallbacks.setMovementFlags(item);
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        IType item = getItem(viewHolder);
        showSwipedItemSnackBar(item, viewHolder.getAbsoluteAdapterPosition() - adapter.getHeadersCount(), direction);
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
        View itemView = viewHolder.itemView;
        if (easyRecyclerView.supportsPullToRefresh())
            easyRecyclerView.enablePullToRefreshLayout(false);
        if ((viewHolder.getAbsoluteAdapterPosition() - adapter.getHeadersCount()) != -1) {
            IType item = getItem(viewHolder);
            if (item != null) {
                Integer moved = (int) dX;
                String actionText = getActionText(item);
                if (dX > 0) {
                    drawer.drawPositive(actionText, itemView, moved, c);
                } else if (dX < 0) {
                    drawer.drawNegative(actionText, itemView, moved, c);
                } else {
                    drawer.clearBounds();
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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

    private void addPendingAction(IType item, Integer position, Integer direction) {
        undoClicked = false;
        pendingAction = () -> {
            if (swipeCallbacks != null) {
                if (undoClicked)
                    swipeCallbacks.cancelAction(item, position);
                else if (direction == ItemTouchHelper.LEFT) {
                    swipeCallbacks.swipedLeftAction(item, position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    swipeCallbacks.swipedRightAction(item, position);
                }
                pendingAction = null;
            }
        };
        actionsHandler.postDelayed(pendingAction, configuration.pendingActionDelay);
    }

    @SuppressWarnings("ShowToast")
    private synchronized void showSwipedItemSnackBar(IType item, Integer position, Integer direction) {
        executePendingAction();
        addPendingAction(item, position, direction);
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

    private IType getItem(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAbsoluteAdapterPosition() - adapter.getHeadersCount();
        return adapter.getItem(position);
    }

    private class SwipeToDeleteDrawerHelper {
        private final Drawable negativeActionIcon;
        private final Drawable positiveActionIcon;
        private final ColorDrawable positiveBackground;
        private final ColorDrawable negativeBackground;

        SwipeToDeleteDrawerHelper(Context context, Modes editMode) {
            if (editMode.equals(Modes.ADD_REMOVE)) {
                positiveActionIcon = ResourceUtils.getDrawable(context, R.drawable.icon_enable);
                negativeActionIcon = ResourceUtils.getDrawable(context, R.drawable.icon_delete);
            } else {
                positiveActionIcon = ResourceUtils.getDrawable(context, R.drawable.icon_enable);
                negativeActionIcon = ResourceUtils.getDrawable(context, R.drawable.icon_disable);
            }
            positiveBackground = new ColorDrawable(configuration.swipePositiveBackgroundColor);
            negativeBackground = new ColorDrawable(configuration.swipeNegativeBackgroundColor);
        }

        void clearBounds() {
            positiveActionIcon.setBounds(0, 0, 0, 0);
            positiveBackground.setBounds(0, 0, 0, 0);
            negativeActionIcon.setBounds(0, 0, 0, 0);
            negativeBackground.setBounds(0, 0, 0, 0);
        }

        void drawPositive(String text, View itemView, Integer dX, Canvas canvas) {
            int backgroundCornerOffset = 20;
            int iconMargin = (itemView.getHeight() - configuration.swipeIconSize) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - configuration.swipeIconSize) / 2;
            int iconBottom = iconTop + configuration.swipeIconSize;
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + configuration.swipeIconSize;
            canvas.clipRect(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dX + backgroundCornerOffset, itemView.getBottom());
            positiveBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            positiveBackground.draw(canvas);
            canvas.save();
            drawPositiveText(text, canvas, itemView, iconRight, iconMargin);
            positiveActionIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            positiveActionIcon.setTint(configuration.swipeAccentColor);
            positiveActionIcon.draw(canvas);
            canvas.restore();
        }

        void drawNegative(String text, View itemView, Integer dX, Canvas canvas) {
            int backgroundCornerOffset = 20;
            int iconMargin = (itemView.getHeight() - configuration.swipeIconSize) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - configuration.swipeIconSize) / 2;
            int iconBottom = iconTop + configuration.swipeIconSize;
            int iconLeft = itemView.getRight() - iconMargin - configuration.swipeIconSize;
            int iconRight = itemView.getRight() - iconMargin;
            canvas.clipRect(itemView.getRight() + dX - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            negativeBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            negativeBackground.draw(canvas);
            canvas.save();
            drawNegativeText(text, canvas, itemView, iconLeft, iconMargin);
            negativeActionIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            negativeActionIcon.setTint(configuration.swipeAccentColor);
            negativeActionIcon.draw(canvas);
            canvas.restore();
        }

        private void drawPositiveText(String text, Canvas canvas, View itemView, Integer iconRight, Integer iconMargin) {
            Paint paint = new Paint();
            paint.setColor(configuration.swipeAccentColor);
            paint.setAntiAlias(true);
            paint.setTextSize(configuration.swipeTextSize);
            paint.setElegantTextHeight(true);
            float yPos = calculateTextYpos(itemView, paint);
            canvas.drawText(text, iconRight + iconMargin, yPos, paint);
        }

        private void drawNegativeText(String text, Canvas canvas, View itemView, Integer iconLeft, Integer iconMargin) {
            Paint paint = new Paint();
            paint.setColor(configuration.swipeAccentColor);
            paint.setAntiAlias(true);
            paint.setTextSize(configuration.swipeTextSize);
            paint.setElegantTextHeight(true);
            float textWidth = paint.measureText(text);
            float yPos = calculateTextYpos(itemView, paint);
            canvas.drawText(text, iconLeft - iconMargin - textWidth, yPos, paint);
        }

        private float calculateTextYpos(View itemView, Paint paint) {
            int itemHeight = itemView.getHeight();
            int itemCenter = itemHeight / 2;
            float textHeight = ((paint.descent() + paint.ascent()) / 2);
            return itemView.getTop() + (itemCenter - textHeight);
        }
    }

    public interface SwipeCallbacks<ItemType extends EasyAdapterDataModel> {

        int setMovementFlags(ItemType item);

        String getActionBackgroundText(ItemType item);

        void swipedLeftAction(ItemType item, Integer position);

        void swipedRightAction(ItemType item, Integer position);

        void cancelAction(ItemType item, Integer position);

        String getPendingActionText(Integer direction);

        String getCancelActionText();
    }

    public enum Modes {
        NON_EDITABLE(1),
        ADD_REMOVE(2),
        ENABLE_DISABLE(3);

        private final int value;
        private static final SparseArray<Modes> values = new SparseArray<>();

        Modes(int value) {
            this.value = value;
        }

        static {
            for (Modes editMode : Modes.values()) {
                values.put(editMode.value, editMode);
            }
        }

        public static Modes valueOf(int pageType) {
            return values.get(pageType);
        }

        public int getValue() {
            return value;
        }
    }

    public static class SwipeConfiguration {
        private static final String EDIT_MODE_TAG = "EDIT_MODE_TAG";
        private static final String UNDO_TEXT_TAG = "UNDO_TEXT_TAG";
        private static final String ICON_SIZE_TAG = "ICON_SIZE_TAG";
        private static final String TEXT_SIZE_TAG = "TEXT_SIZE_TAG";
        private static final String COLOR_ACCENT_TAG = "COLOR_ACCENT_TAG";
        private static final String COLOR_BG_POSITIVE_TAG = "COLOR_BG_POSITIVE_TAG";
        private static final String COLOR_BG_NEGATIVE_TAG = "COLOR_BG_NEGATIVE_TAG";
        private static final String COLOR_BG_SNACKBAR_TAG = "COLOR_BG_SNACKBAR_TAG";
        private static final String SNACKBAR_TEXT_COLOR_TAG = "SNACKBAR_TEXT_COLOR_TAG";
        private Modes editMode;
        private String swipeSnackBarUndoTextPhrase;
        private int swipeAccentColor;
        private int swipeIconSize;
        private int swipeTextSize;
        private int swipePositiveBackgroundColor;
        private int swipeNegativeBackgroundColor;
        private final int pendingActionDelay = 4000;

        public Bundle saveConfigurationState() {
            Bundle savedState = new Bundle();
            savedState.putInt(EDIT_MODE_TAG, editMode.getValue());
            savedState.putString(UNDO_TEXT_TAG, swipeSnackBarUndoTextPhrase);
            savedState.putInt(ICON_SIZE_TAG, swipeIconSize);
            savedState.putInt(TEXT_SIZE_TAG, swipeTextSize);
            savedState.putInt(COLOR_ACCENT_TAG, swipeAccentColor);
            savedState.putInt(COLOR_BG_POSITIVE_TAG, swipePositiveBackgroundColor);
            savedState.putInt(COLOR_BG_NEGATIVE_TAG, swipeNegativeBackgroundColor);
            return savedState;
        }

        public void restoreConfigurationState(Bundle savedState) {
            editMode = Modes.valueOf(savedState.getInt(EDIT_MODE_TAG));
            swipeSnackBarUndoTextPhrase = savedState.getString(UNDO_TEXT_TAG);
            swipeIconSize = savedState.getInt(ICON_SIZE_TAG);
            swipeTextSize = savedState.getInt(TEXT_SIZE_TAG);
            swipeAccentColor = savedState.getInt(COLOR_ACCENT_TAG);
            swipePositiveBackgroundColor = savedState.getInt(COLOR_BG_POSITIVE_TAG);
            swipeNegativeBackgroundColor = savedState.getInt(COLOR_BG_NEGATIVE_TAG);
        }

        public void setEditMode(Modes editMode) {
            this.editMode = editMode;
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

        public Modes getEditMode() {
            return editMode;
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