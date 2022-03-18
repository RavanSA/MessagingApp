package com.project.messagingapp.data.repository.remote

import androidx.lifecycle.MutableLiveData
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.data.model.Response

interface ChatRepository {
    suspend fun createChat(message: String, UID: String,receiverID: String)

    suspend fun checkChat(receiverID: String): Boolean

    suspend fun sendMessage(message: String, receiverID: String)

    fun readMessages (allMessages: List<ChatListModel>) : MutableList<MessageModel>

    suspend fun getConversationUID(receiverID: String): ArrayList<String>

    fun getChatID(receiverID: String): MutableLiveData<Response>
}