package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.model.*
import com.project.messagingapp.data.repository.remote.AppRepo
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatListViewModel(application: Application): AndroidViewModel(application) {
    var chatRepo: ChatRepositoryImpl
    var appRepo: AppRepo

    init {
        val chatListDao = ChatDatabase.getLocalDatabase(application).getChatListRoomDao()
        val chatDao = ChatDatabase.getLocalDatabase(application).getChatRoomDao()
        val contactDao = ChatDatabase.getLocalDatabase(application).getContactListDao()
        chatRepo = ChatRepositoryImpl(chatListDao, chatDao)
        appRepo = AppRepo.SingletonStatic.getInstance(contactDao)
    }

    val currrentUserChatList = liveData(Dispatchers.IO){
        emit(chatRepo.getCurrentUserChatList())
    }


    suspend fun getChatList(chatList: List<ChatListModel>): LiveData<MutableList<ContactChatList>?> {

        val response = liveData(Dispatchers.IO) {
            emit(chatRepo.getChatList(chatList))
        }

        return response
    }

    fun getChatListRoom():MutableList<ChatListRoom>{
        return chatRepo.getChatListRoom()
    }

     fun getContactListRoom(): Flow<MutableList<ContactChatList>> {
        return appRepo.getContactListRoom()
    }

    suspend fun createChatIfNotExist(chatList: ChatListRoom){
        chatRepo.createChatIfNotExist(chatList)
    }

    fun deleteChatList(){
        chatRepo.deleteChatList()
    }

}