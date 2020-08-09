package com.example.clientapp.models

import android.graphics.drawable.Drawable
import android.media.Image
import java.io.Serializable

data class User(
    var nickname: String,
    var avatar: Drawable,
    var whatIdo: String
) : Serializable