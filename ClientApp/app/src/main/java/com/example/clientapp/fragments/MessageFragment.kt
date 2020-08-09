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
import com.example.clientapp.models.Person
import com.example.clientapp.models.User
import com.example.clientapp.network.Client
import com.example.clientapp.recycler.MessageRecyclerViewAdapter
import com.google.android.material.appbar.CollapsingToolbarLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageFragment : Fragment(){

    private var user : User? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null){
            Log.d("connn","aeeeeeeeeeeeeeeee")
            user = requireArguments().getSerializable("user") as User
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.messages, null)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView1)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = MessageRecyclerViewAdapter(findNavController())
        
        val clientRetrofit = Retrofit.Builder().baseUrl("http://localhost:5000/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val clientService: Client = clientRetrofit.create<Client>(Client::class.java)
        clientService.getHistory(user?.nickname!!).enqueue(object : Callback<List<Person>> {
            override fun onResponse(call: Call<List<Person>>, response: retrofit2.Response<List<Person>>) {
                if(response.isSuccessful) {
                    response.body()?.let {
                        (recycler.adapter as MessageRecyclerViewAdapter).setHistory(
                            it
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<Person>>, t: Throwable) {
                Log.d("connectserver", t.message!!)
            }
        })

        return view
    }

}