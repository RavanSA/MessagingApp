package com.project.messagingapp.data.repository.local

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.auth.User
import com.project.messagingapp.data.daos.UserRoomDao
import com.project.messagingapp.data.model.UserRoomModel

class UserRepository(private val userDao: UserRoomDao) {

    suspend fun insertUser(user: UserRoomModel){
        userDao.insertUser(user)
    }

    suspend fun updateUserLocalName(name:String){
        userDao.updateUserLocalName(name)
    }

    suspend fun updateUserLocalProfileImage(newUpdatedProfileImage:String){
        userDao.updateuserLocalProfileImage(newUpdatedProfileImage)
    }

    suspend fun updateUserLocalStatus(updatedStatus: String){
        userDao.updateUserLocalStatus(updatedStatus)
    }

    fun getUserRoom() : LiveData<UserRoomModel>{
       return userDao.getUserRoom()
    }

}