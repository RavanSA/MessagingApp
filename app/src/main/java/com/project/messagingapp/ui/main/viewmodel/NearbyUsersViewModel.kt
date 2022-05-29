package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.remote.AppRepo

class NearbyUsersViewModel(application: Application): AndroidViewModel(application)  {

    private var appRepo: AppRepo

    var nearbyUserLiveData: LiveData<MutableList<UserModel>>
    init {
        val contactListDao = ChatDatabase.getLocalDatabase(application).getContactListDao()
        appRepo = AppRepo.SingletonStatic.getInstance(contactListDao)
       nearbyUserLiveData = appRepo.findNearbyUsersUsingHarvesineDistance()

    }

    fun getNearbyUsersList(): LiveData<MutableList<UserModel>> {
        return nearbyUserLiveData
    }

}