package com.example.clientapp.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rightchat_viewholder.view.*

class ChatRecyclerRightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var message: TextView
    var time: TextView

    init {
        message = itemView.right_message
        time = itemView.right_time
    }

}