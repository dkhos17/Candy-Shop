package com.example.clientapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.baseUrl
import com.example.clientapp.models.Person
import com.example.clientapp.models.User
import com.example.clientapp.network.Client
import com.example.clientapp.recycler.MessageRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.fixedRateTimer


class MessageFragment : Fragment(){

    lateinit var recycler: RecyclerView
    lateinit var user :User
    var list = listOf<Person>()
    val clientRetrofit = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val clientService: Client = clientRetrofit.create<Client>(Client::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            user = requireArguments().getSerializable("user") as User
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.messages, null)
        recycler = view.findViewById(R.id.messages_recyclerview)
        recycler.layoutManager = LinearLayoutManager(context)

        val emptyHisotry = view.findViewById<TextView>(R.id.empty_history)
        recycler.adapter = MessageRecyclerViewAdapter(findNavController(), emptyHisotry, user)


        fixedRateTimer("timer", false, 0L, 3000) {
            clientService.getHistory(user).enqueue(object : Callback<List<Person>> {
                override fun onResponse(
                    call: Call<List<Person>>,
                    response: Response<List<Person>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if(list != it) {
                                list = it
                                (recycler.adapter as MessageRecyclerViewAdapter).setHistory(it.filter { person -> person.messages.isNotEmpty() && person.user.nick != user.nick } as MutableList<Person>)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<Person>>, t: Throwable) {
                    Log.d("connectserver", t.message!!)
                }
            })
        }

        val search = view.findViewById<SearchView>(R.id.search)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0 != null && p0.length >= 3) {
                    var new = list.filter{ it.user.nick.contains(p0, true) && it.user.nick != user.nick}
                    (recycler.adapter as MessageRecyclerViewAdapter).setHistory(new as MutableList<Person>)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null && p0.isEmpty()) {
                    (recycler.adapter as MessageRecyclerViewAdapter).setHistory(list.filter { it.messages.isNotEmpty() && it.user.nick != user.nick} as MutableList<Person>)
                }
                return onQueryTextSubmit(p0)
            }
        })

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recycler)

        return view
    }

    private var simpleCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.RIGHT){
                    (recycler.adapter as MessageRecyclerViewAdapter).deleteItem(position)
                }
            }

        }

}