package com.example.clientapp.models

import android.graphics.drawable.Drawable
import android.media.Image
import java.io.Serializable

data class User(
    val id: Int = 0,
    var nick: String,
    var todo: String,
    var avatar: ByteArray?
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (nick != other.nick) return false
        if (todo != other.todo) return false
        if (avatar != null) {
            if (other.avatar == null) return false
            if (!avatar!!.contentEquals(other.avatar!!)) return false
        } else if (other.avatar != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + nick.hashCode()
        result = 31 * result + todo.hashCode()
        result = 31 * result + (avatar?.contentHashCode() ?: 0)
        return result
    }
}