package com.project.messagingapp.ui.main.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.project.messagingapp.data.repository.remote.AppRepo

class UserRegistrationViewModel : ViewModel(){
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    fun UploadData(username: String,status: String,image: Uri){
        appRepo.UploadData(username,status,image)
    }
}