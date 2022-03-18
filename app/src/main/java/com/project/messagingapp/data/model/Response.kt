package com.project.messagingapp.data.model

data class Response(
    var chatList: List<ChatListModel>? = null,
    var messageList: List<MessageModel>? = emptyList<MessageModel>(),
    var exception: Exception? = null
)