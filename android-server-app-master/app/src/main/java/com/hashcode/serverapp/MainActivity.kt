package com.hashcode.serverapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import androidx.room.Room
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
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
                "GET" -> {
                    sendResponse(exchange, "Welcome to my server")
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
                "GET" -> {
                    // Get all messages
                    val persons = ArrayList<Person>()
                    val inputStream = httpExchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val auth = JSONObject(requestBody) as String
                    val users = database.getUserDao().getAllUsers()
                    for (user in users ){
                        val temp = database.getUserDao().getMessages(user.nick!!, auth)
                        if (temp.isNotEmpty()) persons.add(Person(user, temp))
                    }
                    sendResponse(httpExchange, persons.toString())
                }
                "POST" -> {
                    val inputStream = httpExchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody) as String
                    //for testing
                    sendResponse(httpExchange, "")
                }
            }
        }
    }
}
