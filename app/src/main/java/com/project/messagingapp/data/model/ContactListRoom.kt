package com.project.messagingapp.data.model

import androidx.room.*

@Entity(tableName = "contact_list"
//    foreignKeys = [ForeignKey(
//        entity = ChatListRoom::class,
//        parentColumns = arrayOf("receiver_id"),
//        childColumns = arrayOf("receiver_id"),
//        onDelete = ForeignKey.CASCADE,
//        onUpdate = ForeignKey.CASCADE
//    )],
//    indices = [
//        Index(value = arrayOf("receiver_id"), unique = true)
//    ]
    )
//    indices = [
//        Index(value = arrayOf("receiver_id"), unique = true)
//    ])
data class ContactListRoom(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "receiver_id")
    val receiverID: String = "",
    @ColumnInfo(name = "receiver_Name")
    val name: String = "",
    @ColumnInfo(name = "receiver_phone_number")
    val number: String = "",
    @ColumnInfo(name = "receiver_status")
    val status: String = "",
    @ColumnInfo(name = "receiver_image")
    val image: String = ""

) {
    constructor()
            : this("", "", "","","")
}