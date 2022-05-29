package com.project.messagingapp.data.model

data class UserModel (
    var name: String? = null,
    var status: String?= "",
    val image: String? = "",
    var number: String? = "",
    val uid: String? = "",
    var online: String = "offline",
    var typing: String = "false",
    val token: String = "",
//    val locationLatitude: String = "",
//    val locationLongtitude: String = ""
)
{

}