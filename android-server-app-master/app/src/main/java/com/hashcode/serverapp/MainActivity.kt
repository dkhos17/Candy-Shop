package com.hashcode.serverapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var database: MyDatabase
    private var serverUp = false


    data class Person(
        var user: User,
        var messages: List<Message>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        database = Room.databaseBuilder(this, MyDatabase::class.java, "our_database")
            .fallbackToDestructiveMigration()
            .build()
        val port = 5000
        serverButton.setOnClickListener {
            serverUp = if(!serverUp){
                startServer(port)
                true
            } else{
                stopServer()
                false
            }
        }


//        database.getUserDao().insertUser((User(nick = "xose",todo = "raze", avatar = null)))
//        database.getUserDao().insertUser((User(nick = "pertaxa",todo = "cypher", avatar = null)))
//        database.getUserDao().insertUser((User(nick = "dito",todo = "jett", avatar = null)))
//        database.getUserDao().insertUser((User(nick = "knubo",todo = "omen", avatar = null)))
//        database.getUserDao().insertUser((User(nick = "vacnawt",todo = "sova", avatar = null)))
//
//        database.getUserDao().insertMessage(Message(from = "xose", to = "pertaxa", message = "synack", time = "12:00"))
//        database.getUserDao().insertMessage(Message(from = "pertaxa", to = "xose", message = "ack", time = "12:01"))
//        database.getUserDao().insertMessage(Message(from = "xose", to = "pertaxa", message = "ack", time = "12:02"))
//        database.getUserDao().insertMessage(Message(from = "pertaxa", to = "xose", message = "what's up?", time = "12:03"))
//        database.getUserDao().insertMessage(Message(from = "xose", to = "pertaxa", message = "cucushka", time = "12:04"))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun streamToString(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    private fun sendResponse(httpExchange: HttpExchange, responseText: String){
        httpExchange.sendResponseHeaders(200, responseText.length.toLong())
        val os = httpExchange.responseBody
        os.write(responseText.toByteArray())
        os.close()
    }

    private var mHttpServer: HttpServer? = null


    private fun startServer(port: Int) {
        try {
            mHttpServer = HttpServer.create(InetSocketAddress(port), 0)
            mHttpServer!!.executor = Executors.newCachedThreadPool()

            mHttpServer!!.createContext("/", rootHandler)
            mHttpServer!!.createContext("/check", connectionHandler)
            // Handle /messages endpoint
            mHttpServer!!.createContext("/messages", messageHandler)
            mHttpServer!!.start()//startServer server;
            serverTextView.text = getString(R.string.server_running)
            serverButton.text = getString(R.string.stop_server)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun stopServer() {
        if (mHttpServer != null){
            mHttpServer!!.stop(0)
            serverTextView.text = getString(R.string.server_down)
            serverButton.text = getString(R.string.start_server)
        }
    }

    // Handler for root endpoint
    private val rootHandler = HttpHandler { exchange ->
        run {
            // Get request method
            when (exchange!!.requestMethod) {
                "POST" -> {
                    val ISR = InputStreamReader(exchange.requestBody, "utf-8")
                    val jsonArray = BufferedReader(ISR).use(BufferedReader::readText)
                    val listType = object : TypeToken<User?>() {}.type
                    val usr: User = Gson().fromJson(jsonArray, listType)
                    val datUsr = database.getUserDao().getUser(usr.nick)
                    if (datUsr == null || usr.avatar != null){
                        database.getUserDao().insertUser(usr)
                    }
                    sendResponse(exchange, "")
                }
            }
        }

    }

    private val connectionHandler = HttpHandler { exchange ->
        run {
            // Get request method
            when (exchange!!.requestMethod) {
                "GET" -> {
                    sendResponse(exchange, "")
                }
            }
        }
    }

    private val messageHandler = HttpHandler { httpExchange ->
        run {
            when (httpExchange!!.requestMethod) {
                "POST" -> {

                    val persons = ArrayList<Person>()
                    val ISR = InputStreamReader(httpExchange.requestBody, "utf-8")
                    val jsonArray = BufferedReader(ISR).use(BufferedReader::readText)
                    val listType = object : TypeToken<User?>() {}.type
                    val usr: User = Gson().fromJson(jsonArray, listType)

                    val users = database.getUserDao().getAllUsers()
                    for (user in users){
                        val temp = database.getUserDao().getMessages(user.nick!!, usr.nick)
                        persons.add(Person(user, temp))
                    }
                    //for testing
                    sendResponse(httpExchange, Gson().toJson(persons))
                }
            }
        }
    }
}
