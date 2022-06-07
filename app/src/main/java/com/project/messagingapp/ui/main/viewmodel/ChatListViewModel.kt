package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.model.*
import com.project.messagingapp.data.repository.remote.AppRepo
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class ChatListViewModel(application: Application): AndroidViewModel(application) {
    var chatRepo: ChatRepositoryImpl
    var appRepo: AppRepo

    init {
        val chatListDao = ChatDatabase.getLocalDatabase(application).getChatListRoomDao()
        val chatDao = ChatDatabase.getLocalDatabase(application).getChatRoomDao()
        val contactDao = ChatDatabase.getLocalDatabase(application).getContactListDao()
        val contactChatDao = ChatDatabase.getLocalDatabase(application).getContactChatListDao()

        chatRepo = ChatRepositoryImpl(chatListDao, chatDao,contactDao,contactChatDao)
        appRepo = AppRepo.SingletonStatic.getInstance(contactDao)
    }



    val currrentUserChatList = liveData(Dispatchers.IO){
        emit(chatRepo.getCurrentUserChatList())
    }


    fun getEmotion(): String{
        return appRepo.getEmotion()
    }

    suspend fun getChatList(chatList: List<ChatListModel>): MutableList<ContactChatList>? {
        return chatRepo.getChatList(chatList)
    }

    fun insertContactChatList(contactChatList: ContactChatList){
        return chatRepo.insertContactChatList(contactChatList)
    }

    fun getContacChattList(): Flow<MutableList<ContactChatList>> =
        chatRepo.getContactChatList()

    fun getChatListRoom():List<ChatListRoom>{
        return chatRepo.getChatListRoom()
    }

    fun getContactChatUntilChanged(){
        chatRepo.getContactChatListUntilChanged()
    }

    fun getChatListWithFlow(): Flow<MutableList<ChatListRoom>>{
        return chatRepo.getChatListWithFlow()
    }

    fun contactLastMessageUpdate(date: String, message: String, chatID: String){
        chatRepo.contactLastMessageUpdate(date, message, chatID)
    }


//     fun getContactListRoom(): Flow<MutableList<ContactChatList>> {
//        return appRepo.getContactListRoom()
//    }

    fun getContactListRoom2(): List<ContactListRoom>{
        return appRepo.getContactList()
    }

//    fun getContactListAndChatList(): List<ContactListandChatList>{
//        return appRepo.getContactListAndChatList()
//    }

    suspend fun createChatIfNotExist(chatList: ChatListRoom){
        chatRepo.createChatIfNotExist(chatList)
    }

    suspend fun updateLastMessage(lastMessage: String, date: String,conversationID: String){
        chatRepo.lastMessageOfChat(lastMessage, date,conversationID)
    }

    suspend fun deleteAndCreate(chatList: MutableList<ChatListRoom>){
        chatRepo.deleteAndCreate(chatList)
    }



    fun deleteChatList(){
        chatRepo.deleteChatList()
    }

    fun blockUser(receiverID: String, block: String):Unit{
        return chatRepo.blockUser(receiverID,block)
    }

}