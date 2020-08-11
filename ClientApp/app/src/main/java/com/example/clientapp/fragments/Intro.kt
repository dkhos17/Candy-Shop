package com.example.clientapp.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.clientapp.baseUrl
import com.example.clientapp.models.User
import com.example.clientapp.network.Client
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException


class Intro: Fragment() {
    private val PICK_IMAGE_REQUEST = 1
    private val baos = ByteArrayOutputStream()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.introduce_yourself, container, false)
        val nick = view.findViewById<EditText>(R.id.enterNameText)
        val todo = view.findViewById<EditText>(R.id.todoText)
        val imgView = view.findViewById<ImageView>(R.id.imageView)

        view.findViewById<Button>(R.id.startButton).setOnClickListener {
            if(nick.text.toString().isEmpty() || todo.text.toString().isEmpty()){
                val snack = Snackbar.make(it,"Fill all the information!",Snackbar.LENGTH_LONG)
                snack.show()
                return@setOnClickListener
            }

            val user = User(nick = nick.text.toString(), todo = todo.text.toString(), avatar = baos.toByteArray())

            val clientRetrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
            val clientService: Client = clientRetrofit.create<Client>(Client::class.java)

            clientService.authorizeClient(user).enqueue(object : Callback<Void> {
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
        imgView.setOnClickListener {
            chooseImage()
        }

        return view
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri: Uri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                val imageView: ImageView = activity?.findViewById(R.id.imageView) !!
                imageView.setImageBitmap(bitmap)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}