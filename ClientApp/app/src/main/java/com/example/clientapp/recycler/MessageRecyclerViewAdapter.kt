package com.example.clientapp.recycler

import android.graphics.BitmapFactory
import android.service.autofill.FillEventHistory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.baseUrl
import com.example.clientapp.models.Person
import com.example.clientapp.models.User
import com.example.clientapp.network.Client
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MessageRecyclerViewAdapter(val navigation: NavController, val emptyHistory: TextView, val user: User) : RecyclerView.Adapter<MessageRecyclerViewHolder>() {
    private var data = mutableListOf<Person>()

    val clientRetrofit = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()
    lateinit var clientService: Client

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRecyclerViewHolder {
        return MessageRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        if(data.isEmpty()) {
            emptyHistory.visibility = View.VISIBLE
        } else {
            emptyHistory.visibility = View.INVISIBLE
        }
        return data.size
    }

    override fun onBindViewHolder(holder: MessageRecyclerViewHolder, position: Int) {
        val bt = data[position].user.avatar
        holder.avatar.setImageBitmap(BitmapFactory.decodeByteArray(bt, 0, bt.size))
        if(data[position].messages.isNotEmpty()){
            holder.message.text = data[position].messages.last().message
            holder.time.text = getTimeDiff(Calendar.getInstance().time, data[position].messages.last().date)
        }
        holder.nickname.text = data[position].user.nick
        holder.itemView.setOnClickListener {
            val bndl = bundleOf("person" to data[position])
            bndl.putSerializable("me", user)
            navigation.navigate(R.id.action_messageFragment_to_chat, bndl)
        }

        setLazyHistory(position)
    }


    fun deleteItem(position: Int) {
        Log.d("deeeel", position.toString())
        clientService = clientRetrofit.create<Client>(Client::class.java)
        clientService.deleteMessages(Pair(user, data[position].user)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if(response.isSuccessful) {
                    data.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("connectserver", t.message!!)
            }
        })

    }

    fun setLazyHistory(position: Int) {
        if(position+1 != data.size || data.size % 10 != 0)
            return
        var lazyUser = user
        lazyUser.id = position
        clientService = clientRetrofit.create<Client>(Client::class.java)
        clientService.getLazyHistory(user).enqueue(object : Callback<List<Person>> {
            override fun onResponse(call: Call<List<Person>>, response: Response<List<Person>>) {
                if (response.isSuccessful) {
                    Log.d("load", "lazydata")
                    response.body()?.let {
                        if(position == -1) data = (it.filter { iter -> iter.user.nick != user.nick } as MutableList<Person>)
                        else data.addAll(it.filter { iter -> iter.user.nick != user.nick }  as MutableList<Person>)
                        if (data.isNotEmpty()) data = data.distinct() as MutableList<Person>
                        data = data.filter { iter -> iter.messages.isNotEmpty() } as MutableList<Person>
                        notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<List<Person>>, t: Throwable) {}
        })
    }

    fun setHistory(history: MutableList<Person>) {
        data = history
        notifyDataSetChanged()
    }

    private fun getTimeDiff(date1: Date, date2: Date): String {
        val diff: Long = date1.time - date2.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        if(hours > 0) return "$hours hours"
        return "$minutes min"
    }

}