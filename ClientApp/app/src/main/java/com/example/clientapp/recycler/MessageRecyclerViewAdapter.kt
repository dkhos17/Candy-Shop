package com.example.clientapp.recycler

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.baseUrl
import com.example.clientapp.models.Message
import com.example.clientapp.models.Person
import com.example.clientapp.models.User
import com.example.clientapp.network.Client
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageRecyclerViewAdapter(val navigation: NavController, val emptyHistory: TextView, val user: User) : RecyclerView.Adapter<MessageRecyclerViewHolder>() {
    private var data: MutableList<Person>? = null
    val clientRetrofit = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRecyclerViewHolder {
        return MessageRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        if(data == null || data!!.isEmpty()) {
            emptyHistory.visibility = View.VISIBLE
        } else {
            emptyHistory.visibility = View.INVISIBLE
        }
        if(data == null) {
            return 0
        }
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
            val bndl = bundleOf("person" to data!![position])
            bndl.putSerializable("me", user)
            navigation.navigate(R.id.action_messageFragment_to_chat, bndl)
        }
    }

    fun deleteItem(position: Int) {
        if(data == null) return
        val clientService: Client = clientRetrofit.create<Client>(Client::class.java)
        clientService.deleteMessages(Pair(user, data!![position].user)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if(response.isSuccessful) {
                    data!!.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("connectserver", t.message!!)
            }
        })

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