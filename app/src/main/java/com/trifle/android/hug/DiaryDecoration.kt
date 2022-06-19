package com.trifle.android.hug

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

public class DiaryDecoration(num : Int) : RecyclerView.ItemDecoration() {
    val height : Int = num
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if(parent.getChildAdapterPosition(view) != (parent.adapter!!.itemCount - 1)){
            outRect.bottom = height
        }
    }
}