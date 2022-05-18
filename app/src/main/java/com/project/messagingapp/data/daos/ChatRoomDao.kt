package com.project.messagingapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.messagingapp.data.model.ChatRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun sendNewMessage(chatRoom: ChatRoom): Long

    @Query("SELECT * FROM chat WHERE receiver_id = :receiverID ORDER BY message_date ASC")
    fun getAllMessagesOfChat(receiverID: String) : Flow<MutableList<ChatRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLimitToTen(chatRoom: ChatRoom)

}