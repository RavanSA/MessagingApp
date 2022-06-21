package com.project.messagingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.messagingapp.data.daos.*
import com.project.messagingapp.data.model.*

@Database(entities = arrayOf(UserRoomModel::class, ChatListRoom::class, ChatRoom::class,
        ContactListRoom::class,ContactChatList::class), version = 1, exportSchema = false)
abstract class ChatDatabase:RoomDatabase() {

    abstract fun getUserRoomDao(): UserRoomDao
    abstract fun getChatListRoomDao(): ChatListRoomDao
    abstract fun getChatRoomDao(): ChatRoomDao
    abstract fun getContactListDao(): ContactListDao
    abstract fun getContactChatListDao(): ContactChatDao

    companion object{
        @Volatile
        private var chat_db_instance: ChatDatabase? = null

        fun getLocalDatabase(context: Context): ChatDatabase{

            return chat_db_instance ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_db"
                ).allowMainThreadQueries()
                    .build()
                chat_db_instance = instance
                instance
            }
        }
    }

}