package com.project.messagingapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


//@Entity(
//    tableName = "contact_chat_list",
//
//    foreignKeys = [
//        ForeignKey(entity = ContactListRoom::class, parentColumns = ["receiver_id"], childColumns = ["receiverID"]),
//        ForeignKey(entity = ChatListRoom::class, parentColumns = ["receiverID"], childColumns = ["receiverID"])
//    ]
//)
@Entity(tableName = "contact_chat_list")
data class ContactChatList(
    @PrimaryKey
    val chatID: String= "",
    @ColumnInfo(name = "receiver_id")
    val receiver_id: String = "",
    @ColumnInfo(name = "receiver_name")
    val receiver_Name: String = "",
    @ColumnInfo(name = "receiver_phone_number")
    val receiver_phone_number: String = "",
    @ColumnInfo(name = "receiver_status")
    val receiver_status: String = "",
    @ColumnInfo(name = "receiver_image")
    val receiver_image: String = "",
    @ColumnInfo(name = "uid")
    val uid: String = "",
    @ColumnInfo(name = "message_date")
    val message_date: String = "",
    @ColumnInfo(name = "last_message")
    var lastMessageOfChat: String = "") {
    constructor():
            this("","","","","","",""
            ,"","")

}