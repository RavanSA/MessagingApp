package com.project.messagingapp.data.model

data class ChatResponse(
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

data class UserContactList(
    var userCOntactList: List<UserModel>? = mutableListOf(),
    var exception: Exception? = null
)