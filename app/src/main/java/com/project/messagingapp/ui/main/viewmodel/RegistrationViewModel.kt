package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.model.UserRoomModel
import com.project.messagingapp.data.repository.local.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application):AndroidViewModel(application)  {
    val repository:UserRepository

    init {
        val dao = ChatDatabase.getLocalDatabase(application).getUserRoomDao()
        repository = UserRepository(dao)
    }

    fun insertUser(user:UserRoomModel) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("INSERTVIEWMODEL", user.toString())
        repository.insertUser(user)
    }

    fun updateUserLocalName(name:String) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateUserLocalName(name)
    }

    fun updateUserLocalStatus(updatedStatus: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateUserLocalStatus(updatedStatus)
    }

    fun updatedUserLocalProfileImage(newUpdatedProfileImage: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("ROOMPROFİLE","UPDATED")
        repository.updateUserLocalProfileImage(newUpdatedProfileImage)
    }

     fun getUserRoom():LiveData<UserRoomModel> {
           return repository.getUserRoom()
    }


}

//class RegistrationViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return RegistrationViewModel(application) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}