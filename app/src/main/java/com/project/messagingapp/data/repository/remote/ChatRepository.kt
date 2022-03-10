package com.project.messagingapp.data.repository.remote

import com.google.firebase.database.ValueEventListener
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun createChat(message: String, UID: String,receiverID: String)

    suspend fun checkChat(receiverID: String): Boolean

    suspend fun sendMessage(message: String, receiverID: String)

}