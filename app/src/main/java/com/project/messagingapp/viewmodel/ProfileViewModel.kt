package com.project.messagingapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.messagingapp.Repository.AppRepo
import com.project.messagingapp.UserModel

class ProfileViewModel: ViewModel() {
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    fun getUser() : LiveData<UserModel>{
        return appRepo.getUser()
    }

}