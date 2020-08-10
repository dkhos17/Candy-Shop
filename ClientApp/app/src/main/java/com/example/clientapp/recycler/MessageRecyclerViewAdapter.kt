package com.example.clientapp.recycler

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.models.Person
import com.google.android.material.snackbar.Snackbar

class MessageRecyclerViewAdapter(val navigation: NavController) : RecyclerView.Adapter<MessageRecyclerViewHolder>() {
    private var data: MutableList<Person>? = null

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
        holder.itemView.setOnClickListener {
            val bndl = bundleOf("person" to data!![position])
            navigation.navigate(R.id.action_messageFragment_to_chat, bndl)
        }
    }


    fun setHistory(history: MutableList<Person>) {
        data = history
        notifyDataSetChanged()
    }

    fun getHistory(): MutableList<Person> {
        if(data == null) return listOf<Person>().toMutableList()
        return data!!
    }

}