package com.project.messagingapp.data.repository.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.project.messagingapp.data.model.*
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


class ChatRepositoryImpl: ChatRepository {
    private var conversationID: String? = null
    private var chatIDList:ArrayList<String> = ArrayList<String>()
    private var chatID: String? = null
    private var chatModelList: MutableList<ChatModel>? = mutableListOf()

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

            databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID)

            val messageModel = MessageModel(AppUtil().getUID()!!, receiverID, message, type = "text")
           databaseReference.push().setValue(messageModel)
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
        } catch (e: Exception){
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

                Log.d("CHATMEMBER",chat.member)

                userModel = query.get().await().getValue(UserModel::class.java)!!

                Log.d("USERMODEL",userModel.toString())

                Log.d("CHATID",chat.chatId)
                Log.d("CHATLASTMESSAGE",chat.lastMessage)
                chatModel = ChatModel(
                    chat.chatId,
                    userModel.name,
                    chat.lastMessage,
                    userModel.online
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
        var onlineStatus: String = ""
        var userModel: UserModel? = null
        try {
          onlineStatusQuery.get().await().children.map { snapshot ->
              userModel = snapshot.getValue(UserModel::class.java)
//              onlineStatus = userModel?.online.toString()
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
                    val response = Response()
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
                    messagesLiveData.value = listOfMessages
                }
        messagesLiveData.plusAssign(listOfMessages)
        Log.d("MESSAGESLIVEDATA",messagesLiveData.value.toString())
        return messagesLiveData
    }

    override suspend fun checkChatCreated(receiverID: String): String? {
//        var checkChatMember: Boolean = false
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
                    Log.d("REPOCHECKCHATCREATED", conversationID.toString())
                }
            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
            }
        }
        Log.d("CONVESATIONID",conversationID.toString())
        return conversationID
    }


    override fun getChatID(receiverID: String): MutableLiveData<Response> {
        val mutableLiveData = MutableLiveData<Response>()
        val databaseRef =
            FirebaseDatabase.getInstance().getReference("ChatList").child(AppUtil().getUID()!!)

        val chatQuery = databaseRef.orderByChild("member").equalTo(receiverID)

        chatQuery.get().addOnCompleteListener{ task ->
            val response = Response()
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
                MessageModel(AppUtil().getUID()!!, receiverID, message, System.currentTimeMillis().toString(), "text")

            databaseReference.push().setValue(messageModel)

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
        } catch (e: Exception){
            Log.d("SENDMESSAGE",e.message ?: e.toString())
        }
    }


    operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(values: MutableList<T>) {
        val value = this.value ?: mutableListOf()
        value.addAll(values)
        this.value = value
    }

}