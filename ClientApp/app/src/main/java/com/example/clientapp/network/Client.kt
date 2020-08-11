package com.example.clientapp.network

import com.example.clientapp.models.Message
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

    @POST("direct")
    fun sendMessage(@Body message: Message): Call<Void>

    @POST("messages")
    fun getHistory(@Body nick: User): Call<List<Person>>

    @POST("chat")
    fun getMessages(@Body nick: Pair<User, User>): Call<List<Message>>

    @POST("delete")
    fun deleteMessages(@Body nick: Pair<User, User>): Call<Void>
}