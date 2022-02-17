package com.project.messagingapp.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.messagingapp.data.repository.AppRepo
import com.project.messagingapp.data.model.UserModel

class ProfileViewModel: ViewModel() {
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    fun getUser() : LiveData<UserModel>{
        return appRepo.getUser()
    }

    fun updateStatus(status: String) {
        appRepo.updateStatus(status)
    }

}