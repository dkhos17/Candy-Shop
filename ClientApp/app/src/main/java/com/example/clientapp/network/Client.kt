package com.example.clientapp.network

import com.example.clientapp.models.Person
import com.example.clientapp.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Client {
    @GET("check")
    fun connectServer(): Call<Void>

    @POST("/")
    fun authorizeClient(@Body user: User): Call<Void>

    @POST
    fun sendMessage(): Call<Boolean>

    @GET("messages")
    fun getHistory(@Query("key") nick: String): Call<List<Person>>
}