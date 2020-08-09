package com.hashcode.serverapp

import android.media.Image
import android.widget.ImageView
import androidx.room.*
import java.io.Serializable
import java.util.*


@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nick: String?,
    var todo: String = "",
    var avatar: Image
) : Serializable


@Entity(tableName = "messages", foreignKeys = [ForeignKey(entity = User::class,
    parentColumns = ["id"],
    childColumns = ["id"]
)])
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var from: Int?,
    var to: Int?,
    var message: String?,
    var date: Date
) : Serializable

@Dao
interface UserDao {
    @Query("select * from users where nick = :nickname limit 1")
    fun getUser(nickname: String): User

    @Query("select * from messages where `from` = :key")
    fun getAllMessages(key: Int): List<Message>

    @Query("select * from messages where (`from` = :from and `to` = :to) or (`from` = :to and `to` = :from) ")
    fun getMessages(from: Int, to: Int): List<Message>

    @Query("delete from messages where (`from` = :from and `to` = :to) or (`from` = :to and `to` = :from)")
    fun deleteMessages(from: Int, to: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(us: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(ms: Message)

}

@Database(entities = [User::class, Message::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}
