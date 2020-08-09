package com.example.clientapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.models.Message
import com.example.clientapp.network.Client
import com.example.clientapp.recycler.MessageRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageFragment : Fragment(){

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.messages, null)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView1)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = MessageRecyclerViewAdapter(findNavController())

//        val clientRetrofit = Retrofit.Builder().baseUrl("http://localhost:5000/")
//            .addConverterFactory(GsonConverterFactory.create()).build()
//        val clientService: Client = clientRetrofit.create<Client>(Client::class.java)
//
//        clientService.getMessages().enqueue(object : Callback<List<Message>> {
//            override fun onResponse(call: Call<List<Message>>, response: retrofit2.Response<List<Message>>) {
//                if(response.isSuccessful) {
//
//                }
//            }
//
//            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
//                Log.d("connectserver", t.message!!)
//            }
//        })

        return view
    }

}