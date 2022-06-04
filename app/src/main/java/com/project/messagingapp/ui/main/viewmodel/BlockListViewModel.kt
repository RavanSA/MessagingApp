package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.model.ContactChatList
import com.project.messagingapp.data.repository.remote.AppRepo
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl

class BlockListViewModel(application: Application): AndroidViewModel(application) {
    var chatRepo: ChatRepositoryImpl
    init {
        val chatListDao = ChatDatabase.getLocalDatabase(application).getChatListRoomDao()
        val chatDao = ChatDatabase.getLocalDatabase(application).getChatRoomDao()
        val contactDao = ChatDatabase.getLocalDatabase(application).getContactListDao()
        val contactChatDao = ChatDatabase.getLocalDatabase(application).getContactChatListDao()
        chatRepo = ChatRepositoryImpl(chatListDao, chatDao,contactDao,contactChatDao)
    }

    fun getAllBlockedUser():MutableList<ContactChatList>{
        return chatRepo.getAllBlockedUser()
    }

    fun removeBlock(receiverID: String, block: String){
        return chatRepo.blockUser(receiverID, block)
    }

}