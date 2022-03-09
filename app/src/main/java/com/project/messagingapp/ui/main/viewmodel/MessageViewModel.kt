package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl
import com.project.messagingapp.data.use_case.CheckChat
import com.project.messagingapp.data.use_case.CreateChat
import com.project.messagingapp.data.use_case.UseCases
import kotlinx.coroutines.launch


class MessageViewModel:ViewModel() {

    var chatRepo: ChatRepositoryImpl = ChatRepositoryImpl()

    val isChatChecked: MutableLiveData<Boolean>
        get() = _isChatChecked
    private val _isChatChecked= MutableLiveData<Boolean>()

    val isChatAdded: MutableLiveData<List<ChatListModel>>
        get() = _isChatAdded
    private val _isChatAdded= MutableLiveData<List<ChatListModel>>(emptyList())

    val isMessageSend: MutableLiveData<List<ChatListModel>>
        get() = _isMessageSend
    private val _isMessageSend= MutableLiveData<List<ChatListModel>>(emptyList())

    var createChatVal: Unit? = null

    suspend fun createChat(message: String, UID: String, receiverID: String) {
        viewModelScope.launch {
            createChatVal = chatRepo.createChat(message,UID,receiverID)
//            _isChatChecked.postValue(testCheckChat)
            Log.d("TESTVIEWMODEL",_isChatChecked.value.toString())
        }
    }

        fun checkChat(receiverID: String): MutableLiveData<Boolean> {
            viewModelScope.launch {

               val testCheckChat = chatRepo.checkChat(receiverID)
                _isChatChecked.postValue(testCheckChat)
                Log.d("TESTVIEWMODEL",_isChatChecked.value.toString())
            }
            return _isChatChecked
        }

        suspend fun sendMessage(message: String, receiverID: String, conversationID: String) =
            if(!(chatRepo.checkChat(receiverID))){
                chatRepo.createChat(message, receiverID,receiverID)
                chatRepo.sendMessage(message,receiverID,conversationID)
                Log.d("CHATREPO","TESTED")
            } else {
                chatRepo.sendMessage(message,receiverID,conversationID)
                Log.d("SENDMESSAGE","EXECUTED")
            }
    }
