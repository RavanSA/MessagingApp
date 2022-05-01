package com.project.messagingapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.messagingapp.data.model.ChatListRoom

@Dao
interface ChatListRoomDao {

    @Insert
    suspend fun createChatIfNotExist(chatList: ChatListRoom)

    @Query("UPDATE chat_list SET message_date = :date, lastMessageOfChat = :lastMessage WHERE chatid = :conversationID")
    suspend fun lastMessageOfChat(lastMessage: String, date: String,conversationID: String )


    // SELECT * FROM chat_list
    @Query("SELECT * FROM chat_list")
    fun getChatListRoom(): MutableList<ChatListRoom>

    @Query("DELETE FROM chat_list")
    fun deleteChatList()

}