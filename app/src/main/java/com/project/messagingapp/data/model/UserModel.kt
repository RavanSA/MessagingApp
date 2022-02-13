package com.project.messagingapp.data.model

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

data class UserModel (
    val name: String? = null,
    val status: String?= "",
    val image: String? = "",
    val number: String? = "",
    val UID: String? = ""
)
{

}