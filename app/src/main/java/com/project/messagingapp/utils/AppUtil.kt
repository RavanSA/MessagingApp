package com.project.messagingapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AppUtil {

    fun getUID () : String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
    }

    fun getStorageReference(): StorageReference {
        val storageRef = FirebaseStorage.getInstance().reference
        return storageRef
    }

    fun getDatabaseReferenceUsers(): DatabaseReference{
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Users")
        return databaseReference
    }

    fun getChatQuery(conversationID: String): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("Chat")
            .child(conversationID)
    }

    fun updateOnlineStatus( status: String){
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Users")
        val map = HashMap<String, Any>()
        map["online"] = status
        databaseRef.updateChildren(map)
    }

    fun getTimeAgo(date: Long): String{
        return date.toString()
    }

}