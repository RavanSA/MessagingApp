package com.project.messagingapp.data.repository.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.data.model.Response
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.*
import java.lang.Exception


class ChatRepositoryImpl: ChatRepository {
    private lateinit var conversationID: String
    private var messages: MutableList<MessageModel>? = null
    private var chatIDList:ArrayList<String> = ArrayList<String>()
    private var chatID: String? = null
    private lateinit var job: Job

    override suspend fun createChat(
        message: String,
        UID: String,
        receiverID: String
    )  {
        try {
            var databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(UID)
            val chatID = databaseReference.push().key

            val chatListMode =
                ChatListModel(chatID!!, message, System.currentTimeMillis().toString(), receiverID)
            databaseReference.child(chatID).setValue(chatListMode)

            databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(receiverID)

            val chatList =
                ChatListModel(chatID, message, System.currentTimeMillis().toString(), UID)

            databaseReference.child(chatID).setValue(chatList)

            databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID)

            val messageModel = MessageModel(UID, receiverID, message, type = "text")
           databaseReference.push().setValue(messageModel)
        } catch (e: Exception){
            Log.d("error",e.message ?: e.toString())
         }
    }

    override fun readMessages(allMessages: List<ChatListModel>) : MutableList<MessageModel> {
            val messagesLiveData = MutableLiveData<Response>()
        val listOfMessages: MutableList<MessageModel> = mutableListOf<MessageModel>()

        val query = FirebaseDatabase.getInstance().getReference("Chat")

                query.get().addOnCompleteListener { task ->
                    val response = Response()
                    if(task.isSuccessful) {
                        val result = task.result
//                        result?.let {
//                            //ADD MESSAGE LIST
//                            response.messageList= result.children.map { snapshot ->
//                                snapshot.getValue(MessageModel::class.java)!!
//                            }
//                            val test1 = response.messageList

//                        }
                        result?.let {

                            result.children.map { snapshot ->
//                            snapshot.children
                                val messageListFirebase = snapshot.getValue(MessageModel::class.java)
                                Log.d("FIREBASEMESSAGELIST",messageListFirebase.toString())
//                            messages?.add(messageList!!)
//                                response.messageList?.add(messageListFirebase!!)
                                Log.d("RESPONSE MESSAGELIST", response.messageList.toString())
                                Log.d("SNAPSHOT",snapshot.key.toString())
//                                Log.d("ALLMESSAGES",allMessag)
                                for(i in 1..allMessages.size) {
                                    if(snapshot.key == allMessages[i-1].chatId){
                                        response.messageList = snapshot.children.map { childSnap ->
                                            childSnap.getValue(MessageModel::class.java)!!
                                        }
                                        listOfMessages.addAll(response.messageList!!)
                                    }
                                }
                                Log.d("TESTCHAT",listOfMessages.toString())
                        }
                    }
                } else {
                        response.exception = task.exception
                    }
                    messagesLiveData.value = response
                }
        Log.d("TEST", messagesLiveData.toString())
        Log.d("TEST2", messagesLiveData.value.toString())
        return listOfMessages
    }


    fun testMethod(receiverID: String, member: String):Boolean {
        val checkChat: Boolean
        if(receiverID == member) {
            checkChat = true
        } else {
            checkChat = false
        }
        return checkChat
    }

    override suspend fun checkChat(
        receiverID: String)
    :Boolean {
        val chatQuery = FirebaseDatabase.getInstance().getReference("ChatList")
            .child(receiverID).orderByChild("member").equalTo(receiverID)
        var testMethod = false

        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map { ds ->
                        val member = ds.child("member").value.toString()
                         testMethod = testMethod(receiverID, member)
                            Log.d("TESTMETHOD",testMethod.toString())
                            conversationID = ds.key!!
                            Log.d("CHECKCHATCONVESATIONID",conversationID)
                        GlobalScope.launch {
//                            readMessages(receiverID)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error",error.toString())
            }
        }

        chatQuery.addValueEventListener(listener)
        return testMethod
    }

    override suspend fun getConversationUID(receiverID: String): ArrayList<String> {
        withContext(Dispatchers.Main) {
            val databaseRef =
                FirebaseDatabase.getInstance().getReference("ChatList").child(AppUtil().getUID()!!)
            val chatQuery = databaseRef.orderByChild("member").equalTo(receiverID)
//            .child(receiverID).orderByChild("member").equalTo(receiverID)
//        Log.d("CONVESATIONID","CONVERID")
            var conversationIDCHAT: String? = null
            chatQuery.addValueEventListener(
                object : ValueEventListener {
                    //TODO
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (ds in snapshot.children) {
                                val member = ds.child("member").value.toString()
                                if (member == receiverID) {
                                    conversationIDCHAT = ds.key
//                                    conversationIDCHAT?.let { readMessages(it) }
                                    Log.d("CHATIDLIST", chatIDList.toString())
//                                    GlobalScope.launch {
//                                        suspend {
//                                            Log.d("coroutineScope1", "#runs on ${Thread.currentThread().name}")
//                                            delay(10000)
//                                            withContext(Dispatchers.Main) {
//                                                conversationIDCHAT?.let { chatIDList.add(it) }
//                                                Log.d("coroutineScope2", "#runs on ${Thread.currentThread().name}")
//                                            }
//                                        }.invoke()
//                                    }
                                }
                            }
                        }
////                Log.d("SNAPCONVID",snapshot.toString())
//                        snapshot.children.map { ds ->
////                    Log.d("TEST","TEST")
//                            val member = ds.key
////                    Log.d("MEMBER",member)
//                            conversationIDCHAT = member
//                            chatID = member
//                            Log.d("CONVERSATIONIDCHAT1", conversationIDCHAT.toString())
//                            Log.d("CONVERSATIONIDCHAT2", chatID.toString())
//                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Error", error.toString())
                    }

                })

//        chatQuery.addValueEventListener(listener)
            Log.d("LASTCALLCHATID", chatID.toString())
            Log.d("OUTSIDECHATIDLIST", chatIDList.toString())

        }
            return chatIDList
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
            val map: MutableMap<String, Any> = HashMap()

            map["lastMessage"] = message
            map["date"] = System.currentTimeMillis().toString()

            Log.d("GETUID",AppUtil().getUID()!!)

            var databaseReference =
                FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(AppUtil().getUID()!!).child(conversationID)

            databaseReference.updateChildren(map)

            databaseReference =
                FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID)
                    .child(conversationID)

            databaseReference.updateChildren(map)
        } catch (e: Exception){
            Log.d("SENDMESSAGE",e.message ?: e.toString())
        }
    }


}