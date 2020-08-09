package com.example.clientapp.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R

class MessageRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var avatar: ImageView
    var nickname: TextView
    var time: TextView
    var message: TextView

    init {
        avatar = itemView.findViewById(R.id.icon)
        nickname = itemView.findViewById(R.id.name)
        time = itemView.findViewById(R.id.time)
        message = itemView.findViewById(R.id.last_message)
        itemView.setOnClickListener {
            itemView.findNavController().navigate(R.id.action_messageFragment_to_chat)
        }
    }

}