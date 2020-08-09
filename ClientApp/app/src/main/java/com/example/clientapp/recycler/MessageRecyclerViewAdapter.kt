package com.example.clientapp.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.models.Person
import kotlinx.android.synthetic.main.viewholder.view.*

class MessageRecyclerViewAdapter(val navigation: NavController) : RecyclerView.Adapter<MessageRecyclerViewHolder>() {
    private var data: List<Person>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRecyclerViewHolder {
        return MessageRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        if(data == null)
            return 10
        return data!!.size
    }

    override fun onBindViewHolder(holder: MessageRecyclerViewHolder, position: Int) {
        if(data == null) return

//        holder.itemView.icon. = data!![position].user.avatar
//        holder.itemView.last_message.text = data!![position].messages.last().message
//        holder.itemView.time.text = data!![position].messages.last().date.time.toString()
    }

    fun setHistory(history: List<Person>) {
        data = history
        notifyDataSetChanged()
    }

}