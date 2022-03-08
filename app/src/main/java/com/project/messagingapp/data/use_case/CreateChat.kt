package com.project.messagingapp.data.use_case

import com.project.messagingapp.data.repository.remote.ChatRepository

class CreateChat(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        message: String,
        UID: String,
        receiverID: String
    ) = repository.createChat(message, UID, receiverID)
}

class CheckChat(
    private val repository: ChatRepository
){
    suspend operator fun invoke(
        receiverID: String
    )  = repository.checkChat(receiverID)
}

class SendMessage(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        message: String,
        receiverID: String,
        conversationID: String
    ) = repository.createChat(message,receiverID,conversationID)
}