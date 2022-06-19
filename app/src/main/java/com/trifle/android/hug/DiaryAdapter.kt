package com.trifle.android.hug

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.trifle.android.hug.data.request.DiaryResult
import com.trifle.android.hug.presentation.diary.DetailActivity
import com.trifle.android.hug.presentation.diary.OnDiaryClickListener
import com.trifle.android.hug.presentation.write.WriteActivity

class DiaryAdapter(private var items : List<DiaryResult>)
    : RecyclerView.Adapter<DiaryAdapter.ViewHolder>(), OnDiaryClickListener{

    lateinit var listener : OnDiaryClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_diary,parent,false)
        return ViewHolder(inflatedView,this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
    fun setOnItemClickListener(listener: OnDiaryClickListener){
        this.listener = listener
    }

    override fun onItemClick(holder : ViewHolder, view : View, position : Int){
        listener.onItemClick(holder,view,position)
    }

    override fun onDeleteClick(holder: ViewHolder, view: View, index: Int, position : Int) {
        listener.onDeleteClick(holder,view,index,position)
    }
    override fun getItemCount() = items.size
    fun getItem(position : Int) : DiaryResult? {
        return items[position]
    }
    fun addItem(item : DiaryResult?){
        if (item != null) {
//            items.add(item)
        }
    }

    inner class ViewHolder(itemView : View, listener: OnDiaryClickListener)
        : RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener(View.OnClickListener {
                var position = adapterPosition
                listener.onItemClick(this,itemView,position)
            })
            itemView.findViewById<AppCompatButton>(R.id.btn_delete).setOnClickListener {
                val index = items[adapterPosition].index
                listener.onDeleteClick(this,itemView,index,adapterPosition)

            }
        }
        fun bind(item : DiaryResult){
            setImage(item.emoji) // 이모티콘 설정
            val title = itemView.findViewById<TextView>(R.id.tv_title)
            val date = itemView.findViewById<TextView>(R.id.tv_date)
            title.text = item.title // 제목 설정
            date.text = item.createdAt // 날짜 설정

        }
        fun setImage(emotion : Int){
            val emoji = itemView.findViewById<ImageView>(R.id.img_emo)
            when(emotion){
                0 -> {
                    emoji.setImageResource(R.drawable.ic_happy)
                }
                1 -> {
                    emoji.setImageResource(R.drawable.ic_angry)
                }
                2 -> {
                    emoji.setImageResource(R.drawable.ic_disgust)
                }
                3 -> {
                    emoji.setImageResource(R.drawable.ic_fear)
                }
                4 -> {
                    emoji.setImageResource(R.drawable.ic_neutral)
                }
                5 -> {
                    emoji.setImageResource(R.drawable.ic_sad)
                }
                6 -> {
                    emoji.setImageResource(R.drawable.ic_amazed)
                }
            }
        }
    }
}