package com.project.messagingapp.data.model

import com.google.firebase.database.ServerValue

data class ChatListModel(
    var chatId: String = "",
    var lastMessage: String = "",
    var date: String = "",
    var member: String = ""
)