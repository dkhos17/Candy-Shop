package com.example.clientapp.recycler

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.models.Message
import com.example.clientapp.models.Person
import java.sql.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ChatRecyclerViewAdapter (val navigation: NavController, var person: Person) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        if(person.messages?.get(position)?.from  != person.user.nick) {
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
        return person.messages.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == 0) {
            (holder as ChatRecyclerLeftViewHolder).message.text = person.messages!![position].message
            holder.time.text = person.messages!![position].date.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
        } else {
            (holder as ChatRecyclerRightViewHolder).message.text = person.messages!![position].message
            holder.time.text = person.messages!![position].date.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
        }
    }

    fun sendMessage(message: Message) {
        person.messages.add(message)
        notifyItemInserted(person.messages.size-1)
    }

    fun setMessages(p: Person) {
        person.messages = p.messages
        notifyDataSetChanged()
    }

}
