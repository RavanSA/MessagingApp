package com.project.messagingapp.data.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "chat_list")
//    foreignKeys = [ForeignKey(
//        entity = ContactListRoom::class,
//        parentColumns = arrayOf("receiver_id"),
//        childColumns = arrayOf("receiver_id"),
//        onDelete = CASCADE,
//        onUpdate = CASCADE
//    )],
//    indices = [
//        Index(value = arrayOf("receiver_id"), unique = true)
//    ]
//)
data class ChatListRoom(
    @PrimaryKey
    @ColumnInfo(name = "receiver_id")
    val member: String = "",
    @ColumnInfo(name = "chatid")
    val chatID: String = "",
    @ColumnInfo(name = "message_date")
    val date: String = "",
    @ColumnInfo(name = "lastMessageOfChat")
    val lastMessage: String = "",
    @ColumnInfo(name="uid")
    val uid: String = ""
)
{
    constructor()
            : this("", "", "", "","")
}