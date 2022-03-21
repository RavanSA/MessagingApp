package com.project.messagingapp.data.repository.remote

import androidx.lifecycle.MutableLiveData
import com.project.messagingapp.data.model.*

interface ChatRepository {
    suspend fun createChat(message: String, UID: String,receiverID: String)

    suspend fun checkChat(receiverID: String): Boolean

    suspend fun sendMessage(message: String, receiverID: String)

    fun readMessages (allMessages: List<ChatListModel>) : MutableLiveData<MutableList<MessageModel>>

    fun getChatID(receiverID: String): MutableLiveData<Response>

    suspend fun getCurrentUserChatList() : ChatListResponse

    suspend fun getChatList(chatList: List<ChatListModel>): MutableList<ChatModel>?

}