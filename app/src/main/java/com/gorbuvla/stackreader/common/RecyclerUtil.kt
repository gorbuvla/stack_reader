package com.gorbuvla.stackreader.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by vlad on 20/08/2017.
 */

interface BasicClickListener {
    fun onClick(v: View, pos: Int)
    fun onLongCLick(v: View, pos: Int)
}


class RecyclerListener(context: Context,
                       val recyclerView: RecyclerView,
                       val listener: BasicClickListener
) : RecyclerView.OnItemTouchListener {

    private val detector: GestureDetector

    init {
        detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                val view = recyclerView.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    listener.onLongCLick(view, recyclerView.getChildAdapterPosition(view))
                }
            }
        })
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val view = rv.findChildViewUnder(e.x, e.y)
        if (view != null && detector.onTouchEvent(e)) {
            listener.onClick(view, rv.getChildAdapterPosition(view))
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}



class ScrollListener(private val f: () -> Unit): RecyclerView.OnScrollListener() {

    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var prevItemCount = 0
    private var firstVisibleItem = 0
    private var waitingForData = true


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy > 0) {
            visibleItemCount = recyclerView.layoutManager.childCount
            totalItemCount = recyclerView.layoutManager.itemCount

            firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (waitingForData && totalItemCount > prevItemCount) {

                waitingForData = false
                prevItemCount = totalItemCount
            }

            if (!waitingForData && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 2)) {
                f()
                waitingForData = true
            }
        }
    }
}




class DividerDecoration(context: Context, val orientation: Int) : RecyclerView.ItemDecoration() {

    private val divider: Drawable

    init {
        if (orientation != LinearLayoutManager.HORIZONTAL && orientation != LinearLayoutManager.VERTICAL) {
            throw IllegalArgumentException("invalid orientation")
        }
        val attr = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        divider = attr.getDrawable(0)
        attr.recycle()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(c: Canvas, rv: RecyclerView) {
        val left = rv.paddingLeft
        val right = rv.width - rv.paddingRight

        val childCount = rv.childCount

        for (i in 0 until childCount) {
            val child = rv.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

    private fun drawHorizontal(c: Canvas, rv: RecyclerView) {
        val top = rv.paddingTop
        val bottom = rv.height - rv.paddingBottom

        val childCount = rv.childCount

        for (i in 0 until childCount) {
            val child = rv.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val left = child.right + params.rightMargin
            val right = left + divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }


    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
    }
}


