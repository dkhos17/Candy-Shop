package com.example.clientapp.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.clientapp.R
import com.example.clientapp.models.Person
import com.example.clientapp.models.User
import com.example.clientapp.network.Client
import com.example.clientapp.recycler.MessageRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class Intro: Fragment() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            try {

            } catch (e: Exception){}
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.introduce_yourself, container, false)
        val nick = view.findViewById<EditText>(R.id.editTextTextPersonName)
        val todo = view.findViewById<EditText>(R.id.editTextTextPersonName2)
        val imgView = view.findViewById<ImageView>(R.id.imageView)
        val user = User(nick.text.toString(), imgView.drawable, todo.text.toString())
        view.findViewById<Button>(R.id.startButton).setOnClickListener {
            val clientRetrofit = Retrofit.Builder().baseUrl("http://localhost:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build()
            val clientService: Client = clientRetrofit.create<Client>(Client::class.java)

            clientService.authorizeClient(user.nickname!!).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                    if(response.isSuccessful) {
                        val args = bundleOf("user" to user)
                        findNavController().navigate(R.id.action_intro_to_messageFragment, args)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("connectserver", t.message!!)
                }
            })

        }
        return view
    }
}