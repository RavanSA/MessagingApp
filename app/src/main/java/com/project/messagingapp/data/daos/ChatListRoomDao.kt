package com.project.messagingapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.messagingapp.data.model.ChatListRoom
import com.project.messagingapp.data.model.ChatRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatListRoomDao {

    @Insert
    suspend fun createChatIfNotExist(chatList: ChatListRoom)

    @Query("UPDATE chat_list SET message_date = :date, lastMessageOfChat = :lastMessage WHERE chatid = :conversationID")
    suspend fun lastMessageOfChat(lastMessage: String, date: String,conversationID: String )


    // SELECT * FROM chat_list
    @Query("SELECT * FROM chat_list")
    fun getChatListRoom(): List<ChatListRoom>

    @Query("DELETE FROM chat_list")
    fun deleteChatList()

    @Insert
    suspend fun insertAllChatList(chatList: MutableList<ChatListRoom>)

    @Transaction
    suspend fun deleteAndCreate(chatList: MutableList<ChatListRoom>){
        deleteChatList()
        insertAllChatList(chatList)
    }

    @Query("SELECT * FROM chat_list")
    fun getChatListWithFlow(): Flow<MutableList<ChatListRoom>>

}