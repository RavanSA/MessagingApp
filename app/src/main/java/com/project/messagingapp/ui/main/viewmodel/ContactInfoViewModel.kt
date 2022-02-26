package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.AppRepo
import kotlinx.coroutines.launch

class ContactInfoViewModel: ViewModel() {

    private var appRepo = AppRepo.SingletonStatic.getInstance()

    fun getContactUID(UID: String) : LiveData<UserModel>{
        return appRepo.getContactUID(UID)
    }

}