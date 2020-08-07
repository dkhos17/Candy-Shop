package com.example.clientapp.network

import com.example.clientapp.models.Message
import com.example.clientapp.models.Person
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface Client {
    @GET("check")
    fun connectServer(): Call<Boolean>

    @GET
    fun authorizeClient(): Call<Boolean>

    @POST
    fun sendMessage(): Call<Boolean>

    @GET
    fun getHistory(): Call<List<Person>>

    @GET("messages")
    fun getMessages(): Call<List<Message>>
}