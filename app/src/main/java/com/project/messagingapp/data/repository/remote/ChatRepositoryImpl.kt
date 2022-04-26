package com.project.messagingapp.data.repository.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.database.*
import com.android.volley.Response

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

class ChatRepositoryImpl(
    private val chatListDao: ChatListRoomDao,
    private val chatRoomDao: ChatRoomDao
): ChatRepository {
    private var conversationID: String? = null
    private var chatModelList: MutableList<ChatModel>? = mutableListOf()


    override suspend fun createChatIfNotExist(chatList: ChatListRoom) {
        Log.d("NEWCHATCREATED", chatList.toString())
        chatListDao.createChatIfNotExist(chatList)
    }

    override suspend fun lastMessageOfChat(lastMessage: String, date: String,conversationID: String) {
        Log.d("LASTMESSAGE",lastMessage)
        chatListDao.lastMessageOfChat(lastMessage, date, conversationID)
    }

    override suspend fun addNewMessage(chatRoom: ChatRoom) {
        Log.d("SENDNEWMESSAGEROOM", chatRoom.toString())
        chatRoomDao.sendNewMessage(chatRoom)
    }

    override fun getAllMessagesOfChat(conversationID: String) {
        chatRoomDao.getAllMessagesOfChat(conversationID)
    }

    override fun getChatListRoom(): MutableList<ChatListRoom> {
        return chatListDao.getChatListRoom()
    }



    override suspend fun createChat(
        message: String,
        receiverID: String
    )  {
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
                ChatListModel(chatID, message, System.currentTimeMillis().toString(), AppUtil().getUID()!!)

            databaseReference.child(chatID).setValue(chatList)

            val chatListRoom = ChatListRoom(AppUtil().getUID()!!,chatID,
                System.currentTimeMillis().toString(),message,receiverID)

            createChatIfNotExist(chatListRoom)

            databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID)

            val messageModel = MessageModel(AppUtil().getUID()!!, receiverID, message, type = "text")
           databaseReference.push().setValue(messageModel)


            val chatRoom = ChatRoom(0,chatID,System.currentTimeMillis().toString(),message,
                receiverID,AppUtil().getUID()!!,"text")

            addNewMessage(chatRoom)
        } catch (e: Exception){
            Log.d("error",e.message ?: e.toString())
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

    override suspend fun getChatList(chatList: List<ChatListModel>) : MutableList<ChatModel>? {
        lateinit var chatModel: ChatModel
        lateinit var userModel: UserModel

        try {
            for(chat in chatList) {
                val query = FirebaseDatabase.getInstance().getReference("Users")
                    .child(chat.member)

                userModel = query.get().await().getValue(UserModel::class.java)!!

                chatModel = ChatModel(
                    chat.chatId,
                    userModel.name,
                    chat.lastMessage,
                    userModel.online,
                    chat.member
                )

                chatModelList?.add(chatModel)
            }

        }catch (e: Exception){
            Log.d("ERROR",e.toString())
        }

        return chatModelList
    }

    override suspend fun checkOnlineStatus(receiverID: String): UserModel {
        val onlineStatusQuery = FirebaseDatabase.getInstance(). getReference("Users")
        var userModel: UserModel? = null
        try {
          onlineStatusQuery.get().await().children.map { snapshot ->
              userModel = snapshot.getValue(UserModel::class.java)
          }
        } catch (e: Exception){
            Log.d("ERROR",e.toString())
        }
        return userModel!!
    }

    override fun typingStatus(typing: String) {
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(AppUtil().getUID()!!)
        val map = HashMap<String,Any>()
        map["typing"] = typing
        databaseRef.updateChildren(map)
    }

    override fun readMessages(allMessages: List<ChatListModel>) : MutableLiveData<MutableList<MessageModel>> {
            val messagesLiveData = MutableLiveData<MutableList<MessageModel>>()
        val listOfMessages: MutableList<MessageModel> = mutableListOf<MessageModel>()

        val query = FirebaseDatabase.getInstance().getReference("Chat")

                query.get().addOnCompleteListener { task ->
                    val response = ChatResponse()
                    if(task.isSuccessful) {
                        val result = task.result
                        result?.let {
                            result.children.map { snapshot ->
                                for(i in 1..allMessages.size) {
                                    if(snapshot.key == allMessages[i-1].chatId){
                                        response.messageList = snapshot.children.map { childSnap ->
                                            childSnap.getValue(MessageModel::class.java)!!
                                        }

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

        chatQuery.get().addOnCompleteListener{ task ->
            val response = ChatResponse()
            if(task.isSuccessful){
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
        receiverID: String
    ) {
        try {

            var databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("Chat").child(conversationID!!)

            val messageModel =
                MessageModel(AppUtil().getUID()!!, receiverID, message,
                    System.currentTimeMillis().toString(), "text")

            databaseReference.push().setValue(messageModel)

            val chatRoom  = ChatRoom(0,conversationID!!,System.currentTimeMillis().toString(),message,
            receiverID,AppUtil().getUID()!!,"text")

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

            lastMessageOfChat(message,System.currentTimeMillis().toString(), conversationID!!)
            Log.d("UPDATEMESSAGEROOM","TEST")
        } catch (e: Exception){
            Log.d("SENDMESSAGE",e.message ?: e.toString())
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
                    data.put("title", name)

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
                Log.d("ERRORSENDNOTIDICATIO", "onError: $error")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        Log.d("TOKENVIEWMODEL", request.toString())

        return request
        //TODO RETURN REQUEST
        //TODO IMPLEMENT REQUEST QUEUE IN VIEW
//        val requestQueue = Volley.newRequestQueue(this)
//        request.retryPolicy = DefaultRetryPolicy(
//            30000,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        )
//
//        requestQueue.add(request)
    }


    operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(values: MutableList<T>) {
        val value = this.value ?: mutableListOf()
        value.addAll(values)
        this.value = value
    }

}