package com.project.messagingapp.ui.main.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.messagingapp.data.repository.AppRepo

class UserRegistrationViewModel : ViewModel(){
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    fun UploadData(username: String,status: String,image: Uri){
        appRepo.UploadData(username,status,image)
//            Log.d("USERVIEWMODEL","FAIL!!!!")

    }
}