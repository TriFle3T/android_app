package com.trifle.android.hug.presentation.diary

import android.view.View
import com.trifle.android.hug.DiaryAdapter
import com.trifle.android.hug.ViewPagerAdapter

interface OnDiaryClickListener {
    fun onItemClick(holder : DiaryAdapter.ViewHolder, view : View, position : Int)
    fun onDeleteClick(holder : DiaryAdapter.ViewHolder, view : View, index: Int,position: Int)
}