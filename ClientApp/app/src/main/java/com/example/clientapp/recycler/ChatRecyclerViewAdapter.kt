package com.example.clientapp.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.models.Message

class ChatRecyclerViewAdapter (val navigation: NavController, val owner: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: List<Message>? = null

    override fun getItemViewType(position: Int): Int {
        if(data?.get(position)?.from  == owner) {
            return 1
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 0) {
            return ChatRecyclerLeftViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.leftchat_viewholder, parent, false))
        } else {
            return ChatRecyclerRightViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rightchat_viewholder, parent, false))
        }
    }

    override fun getItemCount(): Int {
        if(data == null)
            return 0
        return data!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(data == null) return

        if(getItemViewType(position) == 0) {
            (holder as ChatRecyclerLeftViewHolder).message.text = data!![position].message
            holder.time.text = data!![position].time
        } else {
            (holder as ChatRecyclerRightViewHolder).message.text = data!![position].message
            holder.time.text = data!![position].time
        }
    }

    fun setMessages(messages: List<Message>) {
        data = messages
        notifyDataSetChanged()
    }

}