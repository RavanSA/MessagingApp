package com.project.messagingapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table")
data class UserRoomModel(
    @PrimaryKey
    val userID: String,
    @ColumnInfo(name = "name")
    val userName: String ,
    @ColumnInfo(name = "number")
    val phoneNumber: String ,
    @ColumnInfo(name = "status")
    val userStatus: String,
    @ColumnInfo(name = "profileImage")
    val profilePhoto: String
)
{
    constructor()
            : this("", "", "", "","")
}