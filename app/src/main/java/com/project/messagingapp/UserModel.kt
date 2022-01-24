package com.project.messagingapp

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

data class UserModel (
    val name: String,
    val status: String,
    val image: String,
    val number: String,
    val UID: String
)
{
    companion object {
        @JvmStatic
        @BindingAdapter("imageSRC")
        fun LoadImage(view: CircleImageView, imageSRC :String?){
            imageSRC?.let{
                Glide.with(view.context).load(imageSRC).into(view)
            }
        }
    }
}