package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.repository.remote.AppRepo
import com.project.messagingapp.data.model.UserModel

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    private var appRepo: AppRepo

    init {
        val contactListDao = ChatDatabase.getLocalDatabase(application).getContactListDao()
        appRepo = AppRepo.SingletonStatic.getInstance(contactListDao)
    }

    fun getUser() : LiveData<UserModel>{
        return appRepo.getUser()
    }

    fun updateStatus(status: String) {
        appRepo.updateStatus(status)
    }

    fun updateName(name: String) {
        appRepo.updateName(name)
    }

    fun updateImage(imageURI: Uri?) {
        appRepo.updateImage(imageURI)
    }

}