package com.project.messagingapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.messagingapp.data.model.ChatRoom

@Dao
interface ChatRoomDao {

    @Insert
    suspend fun sendNewMessage(chatRoom: ChatRoom)

    @Query("SELECT * FROM chat WHERE chat_id = :conversationID")
    fun getAllMessagesOfChat(conversationID: String) : MutableList<ChatRoom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLimitToTen(chatRoom: ChatRoom)

}