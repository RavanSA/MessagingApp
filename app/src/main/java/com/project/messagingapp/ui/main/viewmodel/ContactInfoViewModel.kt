package com.project.messagingapp.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.AppRepo

class ContactInfoViewModel: ViewModel() {

    private var appRepo = AppRepo.SingletonStatic.getInstance()
    val theDate: LiveData<UserModel>
        get() = _theDate
    val _theDate = MutableLiveData<UserModel>()


    fun getContactUID(UID: String?) : MutableLiveData<UserModel>{
        val appRepoUID = appRepo.getContactUID(UID!!)
        _theDate.postValue(appRepoUID)
        Log.d("INVIEWMODEL",_theDate.toString())
        Log.d("INVIEWMODELAPPREPO",appRepoUID.name.toString())
        return _theDate
    }
}