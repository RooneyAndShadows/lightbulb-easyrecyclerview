package com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.item_decorations.base.EasyRecyclerItemDecoration

open class StickyHeaderItemDecoration(
    private val verticalSpacing: Int,
    private val horizontalSpacing: Int,
    private val mListener: StickyHeaderInterface,
) : EasyRecyclerItemDecoration() {
    private var mHeaderHeight: Int? = null

    constructor(listener: StickyHeaderInterface) : this(0, 0, listener)

    @Override
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }
        val headerPos = mListener.getHeaderPositionForItem(topChildPosition)
        val currentHeader = getHeaderViewForItem(headerPos, parent)
        fixLayoutSize(parent, currentHeader)
        val contactPoint = currentHeader.bottom
        val childInContact = getChildInContact(parent, contactPoint, currentHeader)
        if (childInContact != null && mListener.isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, currentHeader, childInContact)
            return
        }
        drawHeader(c, currentHeader)
    }

    private fun getHeaderViewForItem(headerPosition: Int, parent: RecyclerView): View {
        val layoutResId = mListener.getHeaderLayout(headerPosition)
        val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        mListener.bindHeaderData(header, headerPosition)
        return header
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(horizontalSpacing.toFloat(), 0f)
        header.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(horizontalSpacing.toFloat(), (nextHeader.top - currentHeader.height - verticalSpacing).toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int, currentHeader: View): View? {
        var contactPointCopy = contactPoint
        var childInContact: View? = null
        contactPointCopy += verticalSpacing
        for (i in 0 until parent.childCount) {
            var heightTolerance = 0
            val child = parent.getChildAt(i)
            if (currentHeader.hashCode() == child.hashCode()) continue
            //measure height tolerance with child if child is another header
            val isChildHeader = mListener.isHeader(parent.getChildAdapterPosition(child))
            if (isChildHeader) {
                heightTolerance = mHeaderHeight!! - child.height
            }
            heightTolerance += verticalSpacing

            //add heightTolerance if child top be in display area
            val childBottomPosition: Int = if (child.top > 0) child.bottom + heightTolerance
            else child.bottom
            if (childBottomPosition > contactPointCopy)
                if (child.top <= contactPointCopy) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
        }
        return childInContact
    }

    /**
     * Properly measures and layouts the top sticky header.
     *
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private fun fixLayoutSize(parent: ViewGroup, view: View) {
        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        // Specs for children (headers)
        val childWidthSpec =
            ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeightSpec =
            ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)
        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth - horizontalSpacing * 2, view.measuredHeight.also { mHeaderHeight = it })
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = horizontalSpacing
        outRect.right = horizontalSpacing
        outRect.top = verticalSpacing
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) outRect.bottom = verticalSpacing
    }

    interface StickyHeaderInterface {
        /**
         * This method gets called by [StickyHeaderItemDecoration] to fetch the position of the header item in the adapter
         * that is used for (represents) item at specified position.
         *
         * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
         * @return int. Position of the header item in the adapter.
         */
        fun getHeaderPositionForItem(itemPosition: Int): Int

        /**
         * This method gets called by [StickyHeaderItemDecoration] to get layout resource id for the header item at specified adapter's position.
         *
         * @param headerPosition int. Position of the header item in the adapter.
         * @return int. Layout resource id.
         */
        fun getHeaderLayout(headerPosition: Int): Int

        /**
         * This method gets called by [StickyHeaderItemDecoration] to setup the header View.
         *
         * @param header         View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        fun bindHeaderData(header: View?, headerPosition: Int)

        /**
         * This method gets called by [StickyHeaderItemDecoration] to verify whether the item represents a header.
         *
         * @param itemPosition int.
         * @return true, if item at the specified adapter's position represents a header.
         */
        fun isHeader(itemPosition: Int): Boolean
    }
}