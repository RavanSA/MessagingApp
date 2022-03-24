package com.project.messagingapp.data.model

data class UserModel (
    var name: String? = null,
    val status: String?= "",
    val image: String? = "",
    var number: String? = "",
    val uid: String? = "",
    val online: String = "offline",
    val typing: String = "false"
)
{

}