package com.example.clientapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

var baseUrl = "http://10.0.2.2:5000/"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}