package com.example.clientapp.recycler

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
            return 0
        return data!!.size
    }

    override fun onBindViewHolder(holder: MessageRecyclerViewHolder, position: Int) {
        if(data == null) return
        val bt = data!![position].user.avatar
        if (bt != null) {
            holder.avatar.setImageBitmap(BitmapFactory.decodeByteArray(bt, 0, bt.size))
        }
        if(data!![position].messages.isNotEmpty()){
            holder.message.text = data!![position].messages.last().message
            holder.time.text = data!![position].messages.last().time
        }
        holder.nickname.text = data!![position].user.nick
        holder.itemView.setOnClickListener {
            val bndl = Bundle() //bundleOf("person" to data!![position])
            bndl.putString("check","got it")
            navigation.navigate(R.id.action_messageFragment_to_chat, bndl)
        }
    }

    fun deleteItem(position: Int) {
        if(data == null) return
        data!!.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setHistory(history: MutableList<Person>) {
        if(history == null) return
        data = history
        notifyDataSetChanged()
    }

    fun getHistory(): MutableList<Person> {
        if(data == null)
            return listOf<Person>().toMutableList()
        return data!!
    }

}