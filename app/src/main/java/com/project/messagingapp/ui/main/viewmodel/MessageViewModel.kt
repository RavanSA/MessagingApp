package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.use_case.CheckChat
import com.project.messagingapp.data.use_case.CreateChat
import com.project.messagingapp.data.use_case.UseCases
import kotlinx.coroutines.launch


class MessageViewModel:ViewModel() {

    val isChatChecked: MutableLiveData<Boolean>
        get() = _isChatChecked
    private val _isChatChecked= MutableLiveData<Boolean>()

//    val isChatAdded: MutableLiveData<List<ChatListModel>>
//        get() = _isChatAdded
//    private val _isChatAdded= MutableLiveData<List<ChatListModel>>(emptyList())
//
//    val isMessageSend: MutableLiveData<List<ChatListModel>>
//        get() = _isMessageSend
//    private val _isMessageSend= MutableLiveData<List<ChatListModel>>(emptyList())

//    fun createChat(message: String, UID: String,receiverID: String) {
//        viewModelScope.launch {
//            useCases.createChat.invoke(message, UID, receiverID).collect { data ->
//                _isChatAdded.value = data
//            }
//        }
//    }

        fun checkChat(receiverID: String): MutableLiveData<Boolean> {
            viewModelScope.launch {

//               val testCheckChat = useCases!!.checkChat.invoke(receiverID)
//                _isChatChecked.postValue(testCheckChat)
//                Log.d("TESTVIEWMODEL","TESTVIEWMODEL")
            }
            return _isChatChecked
        }

//        fun sendMessage(message: String,receiverID: String,conversationID: String){
//            viewModelScope.launch {
//                useCases.sendMessage.invoke(message, receiverID, conversationID).collect { response ->
//                    _isMessageSend.value = response as Response<Void?>
//                }
//            }
//        }
    }
