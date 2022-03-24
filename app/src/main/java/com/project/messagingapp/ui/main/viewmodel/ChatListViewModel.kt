package com.project.messagingapp.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.ChatModel
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListViewModel: ViewModel() {
    var chatRepo: ChatRepositoryImpl = ChatRepositoryImpl()

    val currrentUserChatList = liveData(Dispatchers.IO){
        emit(chatRepo.getCurrentUserChatList())
    }


    suspend fun getChatList(chatList: List<ChatListModel>): LiveData<MutableList<ChatModel>?> {

        val response = liveData(Dispatchers.IO) {
            emit(chatRepo.getChatList(chatList))
        }

        return response
    }
}