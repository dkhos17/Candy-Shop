package com.example.clientapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.clientapp.network.Client
import kotlinx.android.synthetic.main.checking_connection.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clientRetrofit = Retrofit.Builder().baseUrl("http://localhost:5000/").addConverterFactory(
            GsonConverterFactory.create()).build()
        val clientService: Client = clientRetrofit.create<Client>(Client::class.java)

        clientService.connectServer().enqueue(object : Callback<Boolean?> {
            override fun onResponse(call: Call<Boolean?>, response: retrofit2.Response<Boolean?>) {
                if(response.isSuccessful) {

                }
            }

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                Log.d("fail to connect server", t.message!!)
            }
        })
    }
}