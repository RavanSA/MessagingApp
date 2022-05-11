package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.repository.remote.AppRepo

class UserRegistrationViewModel(application: Application) : AndroidViewModel(application){
    private var appRepo: AppRepo

    init {
        val contactListDao = ChatDatabase.getLocalDatabase(application).getContactListDao()
        appRepo = AppRepo.SingletonStatic.getInstance(contactListDao)
    }

    fun UploadData(username: String,status: String,image: Uri){
        appRepo.UploadData(username,status,image)
    }

}