package com.project.messagingapp.data.repository.remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.JsonObjectRequest
import com.project.messagingapp.data.daos.ContactListDao
import com.project.messagingapp.data.model.*
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

interface ChatRepository {

    suspend fun createChat(message: String,receiverID: String)

    suspend fun sendMessage(message: String, receiverID: String,type: String  ="text")

    fun readMessages (allMessages: List<ChatListModel>) : MutableLiveData<MutableList<MessageModel>>

    fun getChatID(receiverID: String): MutableLiveData<ChatResponse>

    suspend fun getCurrentUserChatList() : ChatListResponse

    suspend fun checkChatCreated(receiverID: String) : String?

    suspend fun getChatList(chatList: List<ChatListModel>): MutableList<ContactChatList>?

    suspend fun checkOnlineStatus(receiverID: String): UserModel

    fun typingStatus(typing: String)

    fun getToken(message: String,receiverID: String,name: String) : JSONObject

    fun sendNotification(to: JSONObject) : JsonObjectRequest

    suspend fun createChatIfNotExist(chatList: ChatListRoom)

    suspend fun lastMessageOfChat(lastMessage: String, date: String, conversationID: String)

    suspend fun addNewMessage(chatRoom: ChatRoom): Long

     fun deleteChatList()

    fun getAllMessagesOfChat(receiverID: String): Flow<MutableList<ChatRoom>>

    fun getChatListRoom() : List<ChatListRoom>

    suspend fun deleteAndCreate(chatList: MutableList<ChatListRoom>)

    fun insertLimitToTen(chatRoom: ChatRoom)

    fun getChatListWithFlow(): Flow<MutableList<ChatListRoom>>

    fun insertContactChatList(contactChatList: ContactChatList)

    fun getContactChatList(): Flow<MutableList<ContactChatList>>

    fun getContactChatListUntilChanged()

    fun contactLastMessageUpdate(date: String, message: String, chatID: String)

    fun getContactListByReceiverID(receiverID: String): ContactListRoom

    suspend fun sendImage(uri: Uri,receiverID: String)

    fun sendVoiceMessage( lastAudioFile: String, receiveID: String)

    fun sendDocumentFile(data: Uri, receiverID: String)

    fun sendCurrentLocation(currentLocation: String, receiverID: String)

    fun blockUser(receiverID: String, block: String) : Unit

    fun getAllBlockedUser(): MutableList<ContactChatList>

}