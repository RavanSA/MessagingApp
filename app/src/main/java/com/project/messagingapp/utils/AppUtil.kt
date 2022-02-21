package com.project.messagingapp.utils

import androidx.fragment.app.Fragment
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

}