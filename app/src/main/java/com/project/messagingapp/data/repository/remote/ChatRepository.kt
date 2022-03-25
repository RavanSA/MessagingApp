package com.project.messagingapp.data.repository.remote

import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.JsonObjectRequest
import com.project.messagingapp.data.model.*
import org.json.JSONObject

interface ChatRepository {
    suspend fun createChat(message: String,receiverID: String)


    suspend fun sendMessage(message: String, receiverID: String)

    fun readMessages (allMessages: List<ChatListModel>) : MutableLiveData<MutableList<MessageModel>>

    fun getChatID(receiverID: String): MutableLiveData<Response>

    suspend fun getCurrentUserChatList() : ChatListResponse

    suspend fun checkChatCreated(receiverID: String) : String?

    suspend fun getChatList(chatList: List<ChatListModel>): MutableList<ChatModel>?

    suspend fun checkOnlineStatus(receiverID: String): UserModel

    fun typingStatus(typing: String)

    suspend fun getToken(message: String,receiverID: String,name: String) : JSONObject

    fun sendNotification(to: JSONObject) : JsonObjectRequest
}