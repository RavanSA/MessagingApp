package com.project.messagingapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat")
data class ChatRoom(
    @PrimaryKey
    val messageID: String = "",
    @ColumnInfo(name = "chat_id")
    val chatID:String,
    @ColumnInfo(name = "message_date")
    val date: String,
    @ColumnInfo(name = "message_of_chat")
    val message:String,
    @ColumnInfo(name = "receiver_id")
    val receiverId:String,
    @ColumnInfo(name = "sender_id")
    val senderId:String,
    @ColumnInfo(name = "type_of_message")
    val type:String
){
    constructor():
            this("","","","","","","")
}
