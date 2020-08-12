package com.example.clientapp.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
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


class MessageFragment : Fragment() {

    lateinit var recycler: RecyclerView
    lateinit var user :User
    lateinit var deleteIcon: Drawable
    lateinit var colorDrawableBackground: ColorDrawable

    var list = listOf<Person>()
    val clientRetrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
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

        deleteIcon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_baseline_close_24) }!!
        colorDrawableBackground = ColorDrawable(Color.parseColor("#FFE91E63"))

        val emptyHisotry = view.findViewById<TextView>(R.id.empty_history)
        recycler.adapter = MessageRecyclerViewAdapter(findNavController(), emptyHisotry, user)
        (recycler.adapter as MessageRecyclerViewAdapter).unpinSearch()
        (recycler.adapter as MessageRecyclerViewAdapter).setLazyHistory(-1)

        val search = view.findViewById<SearchView>(R.id.search)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.d(p0.toString(),"search")
                if (p0 == null ) return false
                (recycler.adapter as MessageRecyclerViewAdapter).pinSearch()

                if(p0.length >= 3) {
                    clientService.searchUser(Pair(p0, user.nick)).enqueue(object : Callback<List<Person>> {
                        override fun onResponse(call: Call<List<Person>>, response: Response<List<Person>>) {
                            if (response.isSuccessful) {
                                Log.d("load", "search")
                                response.body()?.let {
                                    (recycler.adapter as MessageRecyclerViewAdapter).setHistory(it.filter{person -> person.user.nick != user.nick} as MutableList<Person>)
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<Person>>, t: Throwable) {}
                    })
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.d(p0.toString(),"search")

                return onQueryTextSubmit(p0)
            }
        })

        search.setOnCloseListener { onClose() }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recycler)

        return view
    }

    fun onClose(): Boolean {
        Log.d("close","search")
        (recycler.adapter as MessageRecyclerViewAdapter).unpinSearch()
        (recycler.adapter as MessageRecyclerViewAdapter).clearHistory()
        (recycler.adapter as MessageRecyclerViewAdapter).setLazyHistory(-1)
        return true
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

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                } else {
                    colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)
                c.save()

                if (dX > 0) c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                deleteIcon.draw(c)
                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

}