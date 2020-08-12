package com.hashcode.serverapp

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type
import java.util.*

@Entity(tableName = "users", indices = [Index(value = ["nick"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "nick")
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
        result = 31 * result + (nick?.hashCode() ?: 0)
        result = 31 * result + todo.hashCode()
        result = 31 * result + (avatar?.contentHashCode() ?: 0)
        return result
    }
}


@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var from: String,
    var to: String,
    var message: String,
    var date: Date
) : Serializable

class Converter {
    @TypeConverter
    fun stringToDate(data: String?): Date {
        if (data == null) {
            return Date()
        }
        val listType: Type = object : TypeToken<Date?>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun dateToString(someObjects: Date?): String {
        return Gson().toJson(someObjects)
    }
}

@Dao
interface UserDao {
    @Query("select * from users where nick = :nickname limit 1")
    fun getUser(nickname: String): User?

    @Query("select * from users")
    fun getAllUsers(): List<User>

    @Query("select * from users where nick like :nickname")
    fun searchUser(nickname: String): List<User>

    @Query("select u.id,u.nick,u.todo,u.avatar  from users as u join messages as m on m.`from` = :nick or m.`to` = :nick limit 10 offset :offset")
    fun getLimitUsers(nick: String, offset: Int): List<User>

    @Query("select * from messages where `from` = :key")
    fun getAllMessages(key: String): List<Message>

    @Query("select * from messages where (`from` = :from and `to` = :to) or (`from` = :to and `to` = :from) ")
    fun getMessages(from: String, to: String): List<Message>

    @Query("delete from messages where (`from` = :from and `to` = :to) or (`from` = :to and `to` = :from)")
    fun deleteMessages(from: String, to: String)

    @Insert
    fun insertUser(us: User)

    @Insert
    fun insertMessage(ms: Message)

}

@Database(entities = [User::class, Message::class], version = 1)
@TypeConverters(Converter::class)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}
