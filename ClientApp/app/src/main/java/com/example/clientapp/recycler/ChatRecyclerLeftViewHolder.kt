package com.example.clientapp.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.leftchat_viewholder.view.*
import kotlinx.android.synthetic.main.rightchat_viewholder.view.*

class ChatRecyclerLeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var message: TextView
    var time: TextView

    init {
        message = itemView.left_message
        time = itemView.left_time
    }

}