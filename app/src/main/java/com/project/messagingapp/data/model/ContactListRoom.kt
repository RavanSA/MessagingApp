package com.project.messagingapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "contact_list",
    indices = [
        Index(value = arrayOf("receiver_id"), unique = true)
    ])
data class ContactListRoom(

    @PrimaryKey(autoGenerate = false)
    val receiver_id: String = "",
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