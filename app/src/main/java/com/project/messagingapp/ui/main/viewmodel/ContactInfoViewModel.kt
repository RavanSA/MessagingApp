package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.remote.AppRepo
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl

class ContactInfoViewModel(application: Application): AndroidViewModel(application) {

    private var appRepo: AppRepo

    init {
        val contactListDao = ChatDatabase.getLocalDatabase(application).getContactListDao()
        appRepo = AppRepo.SingletonStatic.getInstance(contactListDao)
    }

    val theDate: LiveData<UserModel>
        get() = _theDate
    val _theDate = MutableLiveData<UserModel>()


    fun getContactUID(UID: String?) : MutableLiveData<UserModel>{
        val appRepoUID = appRepo.getContactUID(UID!!)
        _theDate.postValue(appRepoUID)
        return _theDate
    }


}