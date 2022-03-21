package com.project.messagingapp.data.model

data class Response(
    var chatList: List<ChatListModel>? = null,
    var messageList: List<MessageModel>? = emptyList<MessageModel>(),
    var exception: Exception? = null
)

data class ChatListResponse(
    var mainChatList: List<ChatListModel>? = null,
    var exception: Exception? = null
)

data class UserChatList(
    var userChatList: List<ChatModel>? = null,
    var exception: Exception? = null
)