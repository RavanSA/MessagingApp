package com.project.messagingapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.utils.AppUtil

class AppRepo {
    private var liveData: MutableLiveData<UserModel>? = null
    private lateinit var DatabaseRef: DatabaseReference
    private var appUtil = AppUtil()


    object SingletonStatic{
        private var instance: AppRepo? = null
            fun getInstance(): AppRepo{
                if(instance == null)
                    instance = AppRepo()

                return instance!!
            }
    }


    fun getUser(): LiveData<UserModel> {
        if (liveData == null){
            liveData = MutableLiveData()
            DatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(appUtil.getUID()!!)
            DatabaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.exists()){
                     val userModel = snapshot.getValue(UserModel::class.java)
                     liveData!!.postValue(userModel!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
        return liveData!!
    }
}