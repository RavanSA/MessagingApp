package com.project.messagingapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.data.model.UserModel

class ContactViewModelFactory(private val mobileContact:ArrayList<UserModel>) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactViewModel(mobileContact) as T
    }
    }
