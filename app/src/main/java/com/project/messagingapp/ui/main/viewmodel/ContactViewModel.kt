package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.model.UserContactList
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.remote.AppRepo
import kotlinx.coroutines.Dispatchers

class ContactViewModel
constructor (private val mobileContact: ArrayList<UserModel>,
        application: Application): AndroidViewModel(application) {
    private var appRepo: AppRepo

    init {
        val contactListDao = ChatDatabase.getLocalDatabase(application).getContactListDao()
        appRepo = AppRepo.SingletonStatic.getInstance(contactListDao)
    }

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


