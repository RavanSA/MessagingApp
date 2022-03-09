package com.project.messagingapp.data.repository.remote

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.data.model.Response
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class ChatRepositoryImpl: ChatRepository {
    private var chechChatBoolean: Boolean = false


    override suspend fun createChat(
        message: String,
        UID: String,
        receiverID: String)
    {
        try {
//            emit(Response.Loading)
            var databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(UID)
            val chatID = databaseReference.push().key

            val chatListMode =
                ChatListModel(chatID!!, message, System.currentTimeMillis().toString(), receiverID)
            databaseReference.child(chatID!!).setValue(chatListMode)

            databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(receiverID)

            val chatList =
                ChatListModel(chatID!!, message, System.currentTimeMillis().toString(), UID)

            databaseReference.child(chatID).setValue(chatList)

            databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID)

            val messageModel = MessageModel(UID, receiverID, message, type = "text")
           databaseReference.push().setValue(messageModel)
//            emit(Response.Success(createChat))
            Log.d("chatcreated","TRUE")
        } catch (e: Exception){
            Log.d("error",e.message ?: e.toString())
        }

    }


    fun testMethod(receiverID: String, member: String):Boolean {
        var checkChat: Boolean
        if(receiverID == member) {
            checkChat = true
            Log.d("CHECKCHAT",checkChat.toString())
        } else {
            checkChat = false
            Log.d("CHECKCHAT",checkChat.toString())
        }
        return checkChat
    }

    //TODO MAKE checkChat return BOOLEAN
    override suspend fun checkChat(
        receiverID: String)
    :Boolean {
        Log.d("TESTREPOSITORY","TESTREPOSITORY")
        val chatQuery = FirebaseDatabase.getInstance().getReference("ChatList")
            .child(receiverID).orderByChild("member").equalTo(receiverID)
        var testMethod = false

//            this@flow.emit(Response.Loading)
        var conversationID: String? = null
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("SNAPSHOT","ONDATACHANGE")
                snapshot.children.map { ds ->
                        Log.d("DSCHECKCHAT", ds.toString())
                        val member = ds.child("member").value.toString()
                         testMethod = testMethod(receiverID, member)
                            Log.d("TESTMETHOD",testMethod.toString())
//                        if(receiverID == member){
                            conversationID = ds.key.toString()
                            getConversationID(conversationID!!)
//                            chechChatBoolean = true
//                        }
//                    if(conversationID!!.isNotEmpty()){
//                        chechChatBoolean.
//                    }
                }
//                this@flow.emit(Response.Success(items))
            }

            override fun onCancelled(error: DatabaseError) {
//              this@flow.emit(Response.Error(error.message?: error.toString()))
            }
        }

        val UID = AppUtil().getUID()
        FirebaseDatabase.getInstance().getReference("ChatList")
            .child(UID!!).orderByChild("member").equalTo(receiverID)
            .addValueEventListener(listener)

//        this@flow.emit(Response.Success(data))
        return testMethod
    }

    private fun getConversationID(conversationID: String): String? {
        return conversationID
    }

    //TODO if(checkChat == null) createChat else sendMessage
    override suspend fun sendMessage(
        message: String,
        receiverID: String,
        conversationID: String
    ) {
        try{
//            emit(Response.Loading)
            var databaseReference =
                FirebaseDatabase.getInstance().getReference("Chat").child(conversationID)

            val messageModel =
                MessageModel(AppUtil().getUID()!!, receiverID, message,
                    System.currentTimeMillis().toString(), "text")

            databaseReference.push().setValue(messageModel)

            val map: MutableMap<String, Any> = HashMap()

            map["lastMessage"] = message
            map["date"] = System.currentTimeMillis().toString()

            databaseReference =
                FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(AppUtil().getUID()!!).child(conversationID)

            databaseReference.updateChildren(map)

            databaseReference =
                FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID)
                    .child(conversationID)

            databaseReference.updateChildren(map)
//            emit(Response.Success(sendMessage))
        } catch (e: Exception){
//            emit(Error(e.message ?: e.toString()))
            Log.d("exceptıonsendMESSAGE",e.message ?: e.toString())
        }
    }


}