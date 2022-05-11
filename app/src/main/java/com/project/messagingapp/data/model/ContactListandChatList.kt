package com.project.messagingapp.data.model

import androidx.room.Embedded


data class ContactListandChatList(
    @Embedded
    val contactList: ContactListRoom,
    val lastMessageOfChat: String
) {
}