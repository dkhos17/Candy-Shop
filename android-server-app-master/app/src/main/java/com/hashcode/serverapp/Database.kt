package com.hashcode.serverapp

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
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
        result = 31 * result + (nick?.hashCode() ?: 0)
        result = 31 * result + todo.hashCode()
        result = 31 * result + (avatar?.contentHashCode() ?: 0)
        return result
    }
}


@Entity(tableName = "messages", foreignKeys = [ForeignKey(entity = User::class,
    parentColumns = ["id"],
    childColumns = ["id"]
)])
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var from: String,
    var to: String,
    var message: String,
    var time: String
) : Serializable

@Dao
interface UserDao {
    @Query("select * from users where nick = :nickname limit 1")
    fun getUser(nickname: String): User?

    @Query("select * from users")
    fun getAllUsers(): List<User>

    @Query("select * from messages where `from` = :key")
    fun getAllMessages(key: String): List<Message>

    @Query("select * from messages where (`from` = :from and `to` = :to) or (`from` = :to and `to` = :from) ")
    fun getMessages(from: String, to: String): List<Message>

    @Query("delete from messages where (`from` = :from and `to` = :to) or (`from` = :to and `to` = :from)")
    fun deleteMessages(from: String, to: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(us: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(ms: Message)

}

@Database(entities = [User::class, Message::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}
