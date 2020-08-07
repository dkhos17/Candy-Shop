package com.example.clientapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.clientapp.network.Client
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checking_connection)

        val clientRetrofit = Retrofit.Builder().baseUrl("http://localhost:5000/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val clientService: Client = clientRetrofit.create<Client>(Client::class.java)

        clientService.connectServer().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if(response.isSuccessful) {
                    setContentView(R.layout.introduce_yourself)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("connectserver", t.message!!)
            }
        })
    }
}