package com.example.clientapp.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.models.Message
import com.example.clientapp.models.Person

class ChatRecyclerViewAdapter (val navigation: NavController, val person: Person) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        if(person.messages?.get(position)?.from  == person.user.nick) {
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
        if(person.messages == null)
            return 0
        return person.messages!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(person.messages == null) return

        if(getItemViewType(position) == 0) {
            (holder as ChatRecyclerLeftViewHolder).message.text = person.messages!![position].message
            holder.time.text = person.messages!![position].time
        } else {
            (holder as ChatRecyclerRightViewHolder).message.text = person.messages!![position].message
            holder.time.text = person.messages!![position].time
        }
    }
}