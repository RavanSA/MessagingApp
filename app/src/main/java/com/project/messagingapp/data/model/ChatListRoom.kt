package com.project.messagingapp.data.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "chat_list",
    foreignKeys = [ForeignKey(
        entity = ContactListRoom::class,
        parentColumns = arrayOf("receiver_id"),
        childColumns = arrayOf("receiverID"),
        onDelete = CASCADE
    )],
    indices = [
        Index(value = arrayOf("receiverID"), unique = true)
    ])
data class ChatListRoom(
    @PrimaryKey
    val uid: String = "",
    @ColumnInfo(name = "chatid")
    val chatID: String = "",
    @ColumnInfo(name = "message_date")
    val date: String = "",
    @ColumnInfo(name = "lastMessageOfChat")
    val lastMessage: String = "",
    @ColumnInfo(name="receiverID")
    val member: String = ""
)
{
    constructor()
            : this("", "", "", "","")
}