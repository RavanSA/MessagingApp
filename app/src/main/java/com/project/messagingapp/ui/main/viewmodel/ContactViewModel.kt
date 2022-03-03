package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.AppRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel
constructor (private val mobileContact: ArrayList<UserModel>): ViewModel() {
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    val data: MutableLiveData<List<UserModel>>
        get() = _data
    private val _data = MutableLiveData<List<UserModel>>(emptyList())

    fun appContact(): MutableLiveData<List<UserModel>> {
         val contactData = appRepo.getAppContact(mobileContact)
            _data.postValue(contactData)
            Log.d("VIEWMODEL", contactData.toString())

        return _data
    }

}


