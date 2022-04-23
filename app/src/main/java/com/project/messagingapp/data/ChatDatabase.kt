package com.project.messagingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.messagingapp.data.daos.UserRoomDao
import com.project.messagingapp.data.model.UserRoomModel

@Database(entities = arrayOf(UserRoomModel::class), version = 1, exportSchema = false)
abstract class ChatDatabase:RoomDatabase() {
    abstract fun getUserRoomDao():UserRoomDao

    companion object{
        @Volatile
        private var chat_db_instance: ChatDatabase? = null

        fun getLocalDatabase(context: Context): ChatDatabase{

            return chat_db_instance ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_db"
                ).build()
                chat_db_instance = instance
                instance
            }
        }
    }

}