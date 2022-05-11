package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.data.model.UserModel

class ContactViewModelFactory(private val mobileContact:ArrayList<UserModel>,
                              private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactViewModel(mobileContact, application) as T
    }
    }

