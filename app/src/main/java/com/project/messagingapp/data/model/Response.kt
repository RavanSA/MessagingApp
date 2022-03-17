package com.project.messagingapp.data.model

data class Response(
    var chatList: List<ChatListModel>? = null,
    var messageList: List<MessageModel>? = null,
    var exception: Exception? = null
)