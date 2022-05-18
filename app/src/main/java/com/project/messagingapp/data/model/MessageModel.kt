package com.project.messagingapp.data.model

data class MessageModel (
    var messageKey: String ="",
    var chatID: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var date: String = "",
    var type: String = ""
)