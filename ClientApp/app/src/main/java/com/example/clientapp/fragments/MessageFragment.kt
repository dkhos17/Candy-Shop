package com.example.clientapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SearchEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.network.Client
import com.example.clientapp.recycler.MessageRecyclerViewAdapter
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
        val recycler = view.findViewById<RecyclerView>(R.id.messages)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = MessageRecyclerViewAdapter(findNavController())
        
        val clientRetrofit = Retrofit.Builder().baseUrl("http://localhost:5000/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val clientService: Client = clientRetrofit.create<Client>(Client::class.java)

//        clientService.getHistory().enqueue(object : Callback<List<Person>> {
//            override fun onResponse(call: Call<List<Person>>, response: retrofit2.Response<List<Person>>) {
//                if(response.isSuccessful) {
//                    response.body()?.let {
////                        (recycler.adapter as MessageRecyclerViewAdapter).setHistory(it)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<Person>>, t: Throwable) {
//                Log.d("connectserver", t.message!!)
//            }
//        })

        val search = view.findViewById<SearchView>(R.id.search)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0 != null && p0.length >= 3) {
                    var curr = (recycler.adapter as MessageRecyclerViewAdapter).getHistory()
                    var new = curr.filter{ it.user.nickname.contains(p0, true) }
                    recycler.adapter = MessageRecyclerViewAdapter(findNavController())
                    (recycler.adapter as MessageRecyclerViewAdapter).setHistory(new)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return onQueryTextSubmit(p0)
            }
        })

        return view
    }

}