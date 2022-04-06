package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.project.messagingapp.data.model.UserContactList
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.remote.AppRepo
import kotlinx.coroutines.Dispatchers

class ContactViewModel
constructor (private val mobileContact: ArrayList<UserModel>): ViewModel() {
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    val data: MutableLiveData<MutableList<UserModel>>
        get() = _data
    private val _data = MutableLiveData<MutableList<UserModel>>(mutableListOf())

    fun appContact(): LiveData<MutableList<UserModel>?> {
        val response = liveData(Dispatchers.IO) {
            emit(appRepo.getAppContact(mobileContact))
        }

        return response
    }

}


