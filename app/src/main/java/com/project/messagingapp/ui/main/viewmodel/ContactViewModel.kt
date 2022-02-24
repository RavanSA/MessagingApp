package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.AppRepo
import kotlinx.coroutines.Dispatchers

class ContactViewModel: ViewModel() {
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    fun appContact(mobileContact: ArrayList<UserModel>): LiveData<MutableLiveData<UserModel>?> {

        val contactLiveData = liveData(Dispatchers.IO) {
            emit(appRepo.getAppContact(mobileContact))
        }
        return contactLiveData
//        appRepo.getAppContact(mobileContact)!!.observeForever{ contactList ->
//            mutableData!!.value = contactList
        }
    }


