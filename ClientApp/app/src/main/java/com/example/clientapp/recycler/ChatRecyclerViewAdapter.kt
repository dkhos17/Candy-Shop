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
import java.util.*

class ChatRecyclerViewAdapter (val navigation: NavController, val person: Person) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        if(person.messages == null)
            return 0
        return person.messages!!.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(person.messages == null) return

        if(getItemViewType(position) == 0) {
            (holder as ChatRecyclerLeftViewHolder).message.text = person.messages!![position].message
            holder.time.text = getTimeDiff(Calendar.getInstance().time, person.messages!![position].date)
        } else {
            (holder as ChatRecyclerRightViewHolder).message.text = person.messages!![position].message
            holder.time.text = getTimeDiff(Calendar.getInstance().time, person.messages!![position].date)
        }
    }

    fun sendMessage(message: Message) {
        person.messages.add(message)
        notifyItemInserted(person.messages.size-1)
    }

    private fun getTimeDiff(date1: Date, date2: Date): String {
        val diff: Long = date1.time - date2.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        var res = ""
        if(hours < 10) res += "0"
        res += hours.toString()
        res += ":"
        if(minutes < 10) res += "0"
        res += minutes.toString()

        return res
    }
}