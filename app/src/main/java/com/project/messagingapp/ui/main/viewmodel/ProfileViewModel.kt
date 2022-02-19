package com.project.messagingapp.ui.main.viewmodel

import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.project.messagingapp.BuildConfig
import com.project.messagingapp.data.repository.AppRepo
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.ui.main.view.fragments.ProfileFragment
import java.io.File

class ProfileViewModel: ViewModel() {
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    fun getUser() : LiveData<UserModel>{
        return appRepo.getUser()
    }

    fun updateStatus(status: String) {
        appRepo.updateStatus(status)
    }

    fun updateName(name: String) {
        appRepo.updateName(name)
    }



}