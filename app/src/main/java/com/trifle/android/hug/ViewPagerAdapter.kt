package com.trifle.android.hug

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter(private val mList : ArrayList<String>) : RecyclerView.Adapter<ViewPagerAdapter.MyPagerViewHolder>(){
    public val quoteNum : Int = 50
    inner class MyPagerViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val quoteView = itemView.findViewById<TextView>(R.id.list_item)
        fun bind(quote : String){
            quoteView.text = quote
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_quotes,parent,false)
        return MyPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPagerViewHolder, position: Int) {
        if(mList.size > 5) {
            holder.bind(mList[position % quoteNum])
        }else{
            holder.bind(mList[position % 1])
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}