package com.github.rooneyandshadows.lightbulb.easyrecyclerview.decorations;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StickyHeaderItemDecoration extends RecyclerView.ItemDecoration {
    private StickyHeaderInterface mListener;
    private int mStickyHeaderHeight;

    public StickyHeaderItemDecoration(@NonNull StickyHeaderInterface listener) {
        mListener = listener;
    }

    @Override
    public void onDrawOver(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        View topChild = parent.getChildAt(0);
        if (topChild == null) {
            return;
        }
        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return;
        }
        int headerPos = mListener.getHeaderPositionForItem(topChildPosition);
        if (headerPos == -1)
            return;
        View currentHeader = getHeaderViewForItem(headerPos, parent);
        fixLayoutSize(parent, currentHeader);
        int headerBottom = currentHeader.getBottom();
        View childInContact = getChildInContact(parent, headerBottom, headerPos);

        if (childInContact != null && mListener.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, currentHeader, childInContact);
            return;
        }
        drawHeader(c, currentHeader);
    }

    private View getHeaderViewForItem(int headerPosition, RecyclerView parent) {
        int layoutResId = mListener.getHeaderLayout(headerPosition);
        View header = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        mListener.bindHeaderData(header, headerPosition);
        return header;
    }

    private void drawHeader(Canvas c, View header) {
        c.save();
        c.translate(0, 0);
        header.draw(c);
        c.restore();
    }

    private void moveHeader(Canvas c, View currentHeader, View nextHeader) {
        c.save();
        c.translate(0, nextHeader.getTop() - currentHeader.getHeight());
        currentHeader.draw(c);
        c.restore();
    }

    private View getChildInContact(RecyclerView parent, int headerBottom, int currentHeaderPos) {
        View childInContact = null;
        int nextHeaderViewPos = -1;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            int posInAdapter = parent.getChildAdapterPosition(childView);
            if (currentHeaderPos < posInAdapter && posInAdapter == mListener.getHeaderPositionForItem(posInAdapter)) {
                nextHeaderViewPos = i;
                break;
            }
        }
        if (nextHeaderViewPos != -1) {
            View firstChild = parent.getChildAt(0);
            View nextHeaderView = parent.getChildAt(nextHeaderViewPos);
            int nextHeaderTop = nextHeaderView.getTop();
            int firstChildBottom = firstChild.getBottom();
            if (nextHeaderTop <= headerBottom)
                return nextHeaderView;
            else if (firstChildBottom > headerBottom)
                return firstChild;
        }
        return childInContact;
    }


    /**
     * Properly measures and layouts the top sticky header.
     *
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private void fixLayoutSize(ViewGroup parent, View view) {

        // Specs for parent (RecyclerView)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidthSpec, childHeightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), mStickyHeaderHeight = view.getMeasuredHeight());
    }

    public interface StickyHeaderInterface {

        /**
         * This method gets called by {@link StickyHeaderItemDecoration} to fetch the position of the header item in the adapter
         * that is used for (represents) item at specified position.
         *
         * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
         * @return int. Position of the header item in the adapter.
         */
        int getHeaderPositionForItem(int itemPosition);

        /**
         * This method gets called by {@link StickyHeaderItemDecoration} to get layout resource id for the header item at specified adapter's position.
         *
         * @param headerPosition int. Position of the header item in the adapter.
         * @return int. Layout resource id.
         */
        int getHeaderLayout(int headerPosition);

        /**
         * This method gets called by {@link StickyHeaderItemDecoration} to setup the header View.
         *
         * @param header         View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        void bindHeaderData(View header, int headerPosition);

        /**
         * This method gets called by {@link StickyHeaderItemDecoration} to verify whether the item represents a header.
         *
         * @param itemPosition int.
         * @return true, if item at the specified adapter's position represents a header.
         */
        boolean isHeader(int itemPosition);
    }
}