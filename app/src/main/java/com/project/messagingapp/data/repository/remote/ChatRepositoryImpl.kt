package com.project.messagingapp.data.repository.remote

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.utils.AppUtil
import java.lang.Exception

class ChatRepositoryImpl: ChatRepository {
    private lateinit var conversationID: String

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
                            conversationID = ds.key.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error",error.toString())
            }
        }

        chatQuery.addValueEventListener(listener)
        return testMethod
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