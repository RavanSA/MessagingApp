package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.android.volley.toolbox.JsonObjectRequest
import com.project.messagingapp.data.model.*
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.*
import org.json.JSONObject


class MessageViewModel:ViewModel() {

    var chatRepo: ChatRepositoryImpl = ChatRepositoryImpl()

    val readMessageLive = MutableLiveData<MutableList<MessageModel>>(mutableListOf())


    var createChatVal: Unit? = null

        suspend fun createChat(message: String, receiverID: String): LiveData<Unit> {
            val response = liveData(Dispatchers.IO) {
                emit(chatRepo.createChat(message,receiverID))
            }
            return response
        }

        suspend fun sendMessage(message: String, receiverID: String): LiveData<Unit> {
            val response = liveData(Dispatchers.IO) {
                emit(chatRepo.sendMessage(message,receiverID))
            }
            return response
        }

        fun readMessages(allMessages: List<ChatListModel>): MutableLiveData<MutableList<MessageModel>> {
            return chatRepo.readMessages(allMessages)
        }

        fun getChatID(receiverID: String):LiveData<ChatResponse>{
            return chatRepo.getChatID(receiverID)
        }

        suspend fun checkChatCreated(receiverID: String): LiveData<String?> {

        val response = liveData(Dispatchers.IO) {
            emit(chatRepo.checkChatCreated(receiverID))
        }

        return response
    }

    suspend fun checkOnlineStatus(receiverID: String): LiveData<UserModel> {

        val response = liveData(Dispatchers.IO) {
            emit(chatRepo.checkOnlineStatus(receiverID))
        }

        return response
    }

    fun typingStatus(typing: String) : LiveData<Unit> {
        val response = liveData(Dispatchers.IO) {
            emit(chatRepo.typingStatus(typing))
        }

        return response
    }

    fun getToken(
        message: String,
        receiverID: String,
        name: String
    ): LiveData<JSONObject> {

        val response = liveData(Dispatchers.IO) {
            emit(chatRepo.getToken(message,receiverID,name))
        }
        Log.d("TOKENVIEWMODEL", response.toString())

        return response
    }

    fun sendNotification(to: JSONObject): JsonObjectRequest {

        return chatRepo.sendNotification(to)
    }

}