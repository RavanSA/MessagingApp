package com.project.messagingapp.data.repository.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.database.*
import com.android.volley.Response
import com.google.common.io.Files.map
import com.google.firebase.storage.ktx.storageMetadata

import com.project.messagingapp.constants.AppConstants
import com.project.messagingapp.data.model.*
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

import com.project.messagingapp.data.daos.ChatListRoomDao
import com.project.messagingapp.data.daos.ChatRoomDao
import com.project.messagingapp.data.daos.ContactChatDao
import com.project.messagingapp.data.daos.ContactListDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.suspendCoroutine

class ChatRepositoryImpl(
    private val chatListDao: ChatListRoomDao,
    private val chatRoomDao: ChatRoomDao,
    private val contactListDao: ContactListDao,
    private val contactChatDao: ContactChatDao
): ChatRepository {
    private var conversationID: String? = null
    private var chatModelList: MutableList<ContactChatList>? = mutableListOf()
    private var appUtil = AppUtil()

    override fun deleteChatList() {
        chatListDao.deleteChatList()
    }

    override fun insertLimitToTen(chatRoom: ChatRoom) {
        chatRoomDao.insertLimitToTen(chatRoom)
    }

    override suspend fun createChatIfNotExist(chatList: ChatListRoom) {
        Log.d("NEWCHATCREATED", chatList.toString())
        chatListDao.createChatIfNotExist(chatList)
    }

    override suspend fun lastMessageOfChat(
        lastMessage: String,
        date: String,
        conversationID: String
    ) {
        Log.d("LASTMESSAGE", lastMessage)
        Log.d("CONVERSATION", conversationID)
        Log.d("DATE", date)
        chatListDao.lastMessageOfChat(lastMessage, date, conversationID)
    }

    override fun getAllMessagesOfChat(receiverID: String): Flow<MutableList<ChatRoom>> {
        return chatRoomDao.getAllMessagesOfChat(receiverID)
    }

    override suspend fun deleteAndCreate(chatList: MutableList<ChatListRoom>) {
        chatListDao.deleteAndCreate(chatList)
    }

    override suspend fun addNewMessage(chatRoom: ChatRoom): Long {
        Log.d("SENDNEWMESSAGEROOM", chatRoom.toString())
        return chatRoomDao.sendNewMessage(chatRoom)
    }

    override fun insertContactChatList(contactChatList: ContactChatList) {
        contactChatDao.insertContactChatList(contactChatList)
    }

    override fun getContactChatList(): Flow<MutableList<ContactChatList>> {
        return contactChatDao.getContactChatList()
    }

    override fun getChatListRoom(): List<ChatListRoom> {
        return chatListDao.getChatListRoom()
    }

    override fun getChatListWithFlow(): Flow<MutableList<ChatListRoom>> {
        return chatListDao.getChatListWithFlow()
    }

    override fun getContactListByReceiverID(receiverID: String): ContactListRoom {
        return contactListDao.getContactListByReceiverID(receiverID)
    }

    override suspend fun createChat(
        message: String,
        receiverID: String
    ) {
        try {
            var databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(AppUtil().getUID()!!)
            val chatID = databaseReference.push().key

            val chatListMode =
                ChatListModel(chatID!!, message, System.currentTimeMillis().toString(), receiverID)
            databaseReference.child(chatID).setValue(chatListMode)


            databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(receiverID)

            val chatList =
                ChatListModel(
                    chatID,
                    message,
                    System.currentTimeMillis().toString(),
                    AppUtil().getUID()!!
                )

            databaseReference.child(chatID).setValue(chatList)

            val chatListRoom = ChatListRoom(
                AppUtil().getUID()!!, chatID,
                System.currentTimeMillis().toString(), message, receiverID
            )

            createChatIfNotExist(chatListRoom)

            databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID)

            val messageKey = databaseReference.push().key

            val messageModel = messageKey?.let {
                conversationID?.let { it1 ->
                    MessageModel(
                        it,
                        it1, AppUtil().getUID()!!, receiverID, message, type = "text"
                    )
                }
            }

            messageKey?.let { databaseReference.child(it).setValue(messageModel) }


            val chatRoom =
                messageKey?.let {
                    ChatRoom(
                        it, chatID, System.currentTimeMillis().toString(), message,
                        receiverID, AppUtil().getUID()!!, "text"
                    )
                }

            chatRoom?.let { addNewMessage(it) }
            val contactList = getContactListByReceiverID(receiverID)

            val contactChatList = ContactChatList(
                conversationID!!,
                receiverID,
                contactList.name,
                contactList.number,
                contactList.status,
                contactList.image,
                "",
                System.currentTimeMillis().toString(),
                message
            )

            insertContactChatList(contactChatList)
        } catch (e: Exception) {
            Log.d("error", e.message ?: e.toString())
        }
    }

    override suspend fun getCurrentUserChatList(): ChatListResponse {
        val response = ChatListResponse()

        val query = FirebaseDatabase.getInstance().getReference("ChatList")
            .child(AppUtil().getUID()!!)

        try {
            response.mainChatList = query.get().await().children.map { snapshot ->
                snapshot.getValue(ChatListModel::class.java)!!
            }
        } catch (e: Exception) {
            response.exception = e
        }

        return response
    }

    override fun getContactChatListUntilChanged() {
        contactChatDao.getContactChatListUntilChanged()
    }

    override fun contactLastMessageUpdate(date: String, message: String, chatID: String) {
        contactChatDao.contactLastMessageUpdate(date, message, chatID)
    }

    override suspend fun getChatList(chatList: List<ChatListModel>): MutableList<ContactChatList>? {
        lateinit var chatModel: ContactChatList
        lateinit var userModel: UserModel

        try {
            for (chat in chatList) {
                val query = FirebaseDatabase.getInstance().getReference("Users")
                    .child(chat.member)

                userModel = query.get().await().getValue(UserModel::class.java)!!


                chatModel = ContactChatList(
                    chat.chatId,
                    chat.member,
                    userModel.name!!,
                    userModel.number!!,
                    userModel.status!!,
                    userModel.image!!,
                    AppUtil().getUID()!!,
                    System.currentTimeMillis().toString(),
                    chat.lastMessage
                )

                chatModelList?.add(chatModel)
            }

        } catch (e: Exception) {
            Log.d("ERROR", e.toString())
        }

        return chatModelList
    }

    override suspend fun checkOnlineStatus(receiverID: String): UserModel {
        val onlineStatusQuery = FirebaseDatabase.getInstance().getReference("Users")
        var userModel: UserModel? = null
        try {
            onlineStatusQuery.get().await().children.map { snapshot ->
                userModel = snapshot.getValue(UserModel::class.java)
            }
        } catch (e: Exception) {
            Log.d("ERROR", e.toString())
        }
        return userModel!!
    }

    override fun typingStatus(typing: String) {
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(AppUtil().getUID()!!)
        val map = HashMap<String, Any>()
        map["typing"] = typing
        databaseRef.updateChildren(map)
    }

    override fun readMessages(allMessages: List<ChatListModel>): MutableLiveData<MutableList<MessageModel>> {
        val messagesLiveData = MutableLiveData<MutableList<MessageModel>>()
        val listOfMessages: MutableList<MessageModel> = mutableListOf<MessageModel>()

        val query = FirebaseDatabase.getInstance().getReference("Chat")
        query.get().addOnCompleteListener { task ->
            val response = ChatResponse()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    result.children.map { snapshot ->
                        for (i in 1..allMessages.size) {
                            if (snapshot.key == allMessages[i - 1].chatId) {
                                response.messageList = snapshot.children.map { childSnap ->
                                    Log.d("KEYS", childSnap.key.toString())
                                    childSnap.getValue(MessageModel::class.java)!!
                                }

//                                        snapshot.key!!.map { childSnap ->
//
//                                        }
//                                        Log.d("MESSAGEKEYS", messageKeys.toString())

                                listOfMessages.addAll(response.messageList!!)
                            }
                        }
                    }
                }
            } else {
                response.exception = task.exception
            }
            messagesLiveData.postValue(listOfMessages)
        }
        messagesLiveData.plusAssign(listOfMessages)
        return messagesLiveData
    }

    override suspend fun checkChatCreated(receiverID: String): String? {
        var member: String? = null
        val chatQuery = FirebaseDatabase.getInstance().getReference("ChatList")
            .child(AppUtil().getUID()!!).orderByChild("member").equalTo(receiverID)
        withContext(Dispatchers.Main) {
            try {
                chatQuery.get().await().children.map { snapshot ->
                    for (ds in snapshot.children) {
                        member = ds.value.toString()
                        if (member == receiverID) {
                            conversationID = snapshot.key.toString()
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
            }
        }
        return conversationID
    }


    override fun getChatID(receiverID: String): MutableLiveData<ChatResponse> {
        val mutableLiveData = MutableLiveData<ChatResponse>()
        val databaseRef =
            FirebaseDatabase.getInstance().getReference("ChatList").child(AppUtil().getUID()!!)
        //SELECT * FROM chat_list  WHERE uid = user.uid and member = receiverID
        val chatQuery = databaseRef.orderByChild("member").equalTo(receiverID)

        chatQuery.get().addOnCompleteListener { task ->
            val response = ChatResponse()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.chatList = result.children.map { snapshot ->
                        snapshot.getValue(ChatListModel::class.java)!!
                    }
                }
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }
        return mutableLiveData
    }

    override suspend fun sendMessage(
        message: String,
        receiverID: String,
        type: String
    ) {
        try {
            var databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("Chat").child(conversationID!!)

            val messageKey = databaseReference.push().key

            val messageModel =
                messageKey?.let {
                    MessageModel(
                        it, conversationID!!, AppUtil().getUID()!!, receiverID, message,
                        System.currentTimeMillis().toString(), type
                    )
                }

            databaseReference.child(messageKey!!).setValue(messageModel)

            Log.d("MESSAGEKEY ", messageKey.toString())


            val chatRoom = ChatRoom(
                messageKey, conversationID!!, System.currentTimeMillis().toString(), message,
                receiverID, AppUtil().getUID()!!, type
            )

            addNewMessage(chatRoom)
            Log.d("ADDNEWMESSAGEROOM", chatRoom.toString())
            val map: MutableMap<String, Any> = HashMap()

            map["lastMessage"] = message
            map["date"] = System.currentTimeMillis().toString()

            databaseReference =
                FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(AppUtil().getUID()!!).child(conversationID!!)

            databaseReference.updateChildren(map)

            databaseReference =
                FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID)
                    .child(conversationID!!)

            databaseReference.updateChildren(map)

            lastMessageOfChat(message, System.currentTimeMillis().toString(), conversationID!!)

            contactLastMessageUpdate(
                System.currentTimeMillis().toString(),
                message,
                conversationID!!
            )
            Log.d(
                "UPDATEMESSAGEROOM",
                lastMessageOfChat(
                    message,
                    System.currentTimeMillis().toString(),
                    conversationID!!
                ).toString()
            )
        } catch (e: Exception) {
            Log.d("SENDMESSAGE", e.message ?: e.toString())
        }
    }

    override fun getToken(
        message: String,
        receiverID: String,
        name: String
    ): JSONObject {


        val to = JSONObject()
        val data = JSONObject()

        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(receiverID)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val token = snapshot.child("token").value.toString()
                Log.d("TOKEN", token)

                data.put("receiverID", receiverID)
                data.put("message", message)
                data.put("conversationID", conversationID)
                data.put("name", name)

                to.put("to", token)
                to.put("data", data)
                Log.d("TOPUT", to.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FCMERROR", error.details)
            }

        })


        Log.d("TONOTIFICATION", to.toString())

        return to

    }

    override fun sendNotification(to: JSONObject): JsonObjectRequest {
        Log.d("TOSEND", to.toString())
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            AppConstants.NOTIFICATION_URL,
            to,
            Response.Listener { response: JSONObject ->

                Log.d("SENDNOTICATIONREPO", "onResponse: $response")
            },
            Response.ErrorListener { error ->
                Log.d(
                    "ERRORSENDINGNOTI", "Error: " + error
                            + "\nStatus Code " + error.networkResponse.statusCode
                            + "\nResponse Data " + error.networkResponse.data
                            + "\nlocalizedmessage " + error.localizedMessage
                            + "\nmessage" + error.message
                            + "\ncause" + error.cause
                            + "\nsuppressed" + error.suppressed
                            + "\ntimems" + error.networkTimeMs
                )
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                Log.d("JSONCONTECTTEST", map.toString())
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        Log.d("TOKENVIEWMODEL", request.toString())

        return request
    }

    fun deleteMessageFromFirebase(chatID: String, messageKey: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("Chat")
            .child(chatID).child(messageKey)

        databaseRef.removeValue()
    }

    override suspend fun sendImage(uri: Uri, receiverID: String) {
        var imageUrl = ""
//        val currentUserReference = appUtil.getDatabaseReferenceUsers().
//        child(appUtil.getUID()!!)
//        userUploadData = MutableLiveData()
        conversationID?.let { it1 ->
            appUtil.getStorageReference().child(AppConstants.chatPath).child(it1).child(System.currentTimeMillis().toString()).putFile(uri)
                .addOnSuccessListener {
                    val task = it.storage.downloadUrl
                    task.addOnCompleteListener { uri ->
                        imageUrl = uri.result.toString()
                        Log.d("APPREPOIMAGE", imageUrl)
                        Log.d("IMAGE UPLOADED", "TRUE")
                        GlobalScope.launch(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                if (!imageUrl.isNullOrEmpty()) {
                                    sendMessage(imageUrl, receiverID, "image")
                                    Log.d("MESSAGESENDED", "TRUE")
                                }
                            }
                        }
                    }
                }
        }


    }

    override fun sendVoiceMessage(lastAudioFile: String, receiverID: String) {

        var voiceURL = ""

        val metadata = storageMetadata {
            contentType = "audio/mpeg"
        }

//        val voiceUri: Uri = Uri.parse(lastAudioFile)
        val voiceUri: Uri = Uri.fromFile(File(lastAudioFile))

        Log.d("VOICEURI",voiceUri.toString())

        conversationID?.let {
            appUtil.getStorageReference().child(AppConstants.chatPath).child(it).child(System.currentTimeMillis().toString()).putFile(voiceUri, metadata)
                .addOnSuccessListener {
                    val task = it.storage.downloadUrl
                    task.addOnCompleteListener { uri ->
                        voiceURL = uri.result.toString()
                        Log.d("VOİCE", voiceURL)
                        Log.d("IMAGE UPLOADED", "TRUE")
                        GlobalScope.launch(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                if (!voiceURL.isNullOrEmpty()) {
                                    sendMessage(voiceURL, receiverID, "audio")
                                    Log.d("MESSAGESENDED", "TRUE")
                                }
                            }
                        }
                    }
                }
        }
    }

    override fun sendDocumentFile(data: Uri, receiverID: String) {
        var fileUrl: String = ""

        conversationID?.let {
            appUtil.getStorageReference().child(AppConstants.chatPath).child(it).child(System.currentTimeMillis().toString()).putFile(data)
                .addOnSuccessListener {
                    val task = it.storage.downloadUrl
                    task.addOnCompleteListener { uri ->
                        fileUrl = uri.result.toString()
                        Log.d("VOİCE", fileUrl)
                        Log.d("IMAGE UPLOADED", "TRUE")
                        GlobalScope.launch(Dispatchers.IO) {
                            withContext(Dispatchers.IO) {
                                if (!fileUrl.isNullOrEmpty()) {
                                    sendMessage(fileUrl, receiverID, "document")
                                    Log.d("MESSAGESENDED", "TRUE")
                                }
                            }
                        }
                    }
                }
        }
    }

    operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(values: MutableList<T>) {
        val value = this.value ?: mutableListOf()
        value.addAll(values)
        this.value = value
    }


}