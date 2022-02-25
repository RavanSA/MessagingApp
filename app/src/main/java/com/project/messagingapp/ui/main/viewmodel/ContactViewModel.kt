package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.AppRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel
   constructor (private val mobileContact: ArrayList<UserModel>): ViewModel() {
    private var appRepo = AppRepo.SingletonStatic.getInstance()

    val data: LiveData<List<UserModel>>
        get() = _data
    private val _data = MutableLiveData<List<UserModel>>(emptyList())

    init {
        appContact()
    }


    fun appContact() {

        viewModelScope.launch {
            val contactData = appRepo.getAppContact(mobileContact)
                _data.postValue(contactData!!)
                Log.d("VIEWMODEL", contactData.toString())

        }
//        val contactLiveData = liveData(Dispatchers.IO) {
//            emit(appRepo.getAppContact(mobileContact))
//        }
//        return contactLiveData
//        appRepo.getAppContact(mobileContact)!!.observeForever{ contactList ->
//            mutableData!!.value = contactList
        }
    }


