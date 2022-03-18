package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.data.model.Response
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.*


class MessageViewModel:ViewModel() {

    var chatRepo: ChatRepositoryImpl = ChatRepositoryImpl()

    val isChatChecked: MutableLiveData<Boolean>
        get() = _isChatChecked
    private val _isChatChecked= MutableLiveData<Boolean>()

    val isChatAdded: MutableLiveData<List<ChatListModel>>
        get() = _isChatAdded
    private val _isChatAdded= MutableLiveData<List<ChatListModel>>(null)

    val isMessageSend: MutableLiveData<List<ChatListModel>>
        get() = _isMessageSend
    private val _isMessageSend= MutableLiveData<List<ChatListModel>>(emptyList())

    val messages: MutableLiveData<List<MessageModel>?>
        get() = _messages
    private val _messages = MutableLiveData<List<MessageModel>?>(emptyList())

    val conversationList: MutableLiveData<ArrayList<String>?>
        get() = _conversationList
    private val _conversationList = MutableLiveData<ArrayList<String>?>()

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

        suspend fun sendMessage(message: String, receiverID: String) =
            if(!chatRepo.checkChat(receiverID)){
                chatRepo.createChat(message, AppUtil().getUID()!!,receiverID)
                chatRepo.sendMessage(message,receiverID)

//                chatRepo.readMessages(receiverID)
                Log.d("CHATREPO","TESTED")
                Log.d("CHECKCHAT", chatRepo.checkChat(receiverID).toString())
            } else {
                chatRepo.sendMessage(message,receiverID)
                Log.d("SENDMESSAGE","EXECUTED")
            }

        fun readMessages(allMessages: List<ChatListModel>): MutableList<MessageModel> {
            return chatRepo.readMessages(allMessages)
        }

        fun getChatID(receiverID: String):LiveData<Response>{
            return chatRepo.getChatID(receiverID)
        }

    }