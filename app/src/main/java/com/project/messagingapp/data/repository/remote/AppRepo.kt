package com.project.messagingapp.data.repository.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.project.messagingapp.constants.AppConstants
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.utils.AppUtil

class AppRepo {
    private var liveData: MutableLiveData<UserModel>? = null
    private var contactUserData: UserModel = UserModel()
    private var appContacts: MutableList<UserModel>? = null
    private var appUtil = AppUtil()
    private var userUploadData: MutableLiveData<FirebaseDatabase>? = null
    private var messages: MutableList<MessageModel>? = null

    object SingletonStatic{
        private var instance: AppRepo? = null

            fun getInstance(): AppRepo {
                if(instance == null)
                    instance = AppRepo()
                return instance!!
            }

    }


    fun getUser(): LiveData<UserModel> {
        if (liveData == null){
            liveData = MutableLiveData()
            appUtil.getDatabaseReferenceUsers().child(appUtil.getUID()!!)
                .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.exists()){
                     val userModel = snapshot.getValue(UserModel::class.java)
                     liveData!!.postValue(userModel!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR",error.toString())
                }
            })
        }

        return liveData!!
    }

     fun UploadData(
         username: String,
         status: String,
         image: Uri
     ) {
         val currentUserReference = appUtil.getDatabaseReferenceUsers().
         child(appUtil.getUID()!!)
         userUploadData = MutableLiveData()

            appUtil.getUID()!!.let { it ->
                appUtil.getStorageReference().child(AppConstants.Path).child(it).putFile(image)
                .addOnSuccessListener {
                    val task = it.storage.downloadUrl
                    task.addOnCompleteListener { uri ->
                        val imageUrl = uri.result.toString()
                        Log.d("APPREPOIMAGE",imageUrl)
                        val map = mapOf(
                            "name" to username,
                            "status" to status,
                            "image" to imageUrl
                        )
                        currentUserReference.updateChildren(map)
                    }
                }
            }
     }

    fun updateStatus(status: String) {
        val map : Map<String, Any> = mapOf(
            "status" to status
         )

        appUtil.getDatabaseReferenceUsers().child(appUtil.getUID()!!)
            .updateChildren(map)
    }

    fun updateName(name: String) {
        val map : Map<String, Any> = mapOf(
            "name" to name
        )

        appUtil.getDatabaseReferenceUsers().child(appUtil.getUID()!!)
            .updateChildren(map)
    }

    fun updateImage(imageURI: Uri?) {
        val databaseRef = appUtil.getDatabaseReferenceUsers().
        child(appUtil.getUID()!!)

        appUtil.getUID()!!.let { it ->
            appUtil.getStorageReference().child(AppConstants.Path).child(it).putFile(imageURI!!)
                .addOnSuccessListener {
                    val task = it.storage.downloadUrl
                    task.addOnCompleteListener { uri ->
                        val imageUrl = uri.result.toString()
                        val map = mapOf(
                            "image" to imageUrl
                        )
                        databaseRef.updateChildren(map)
                    }
                }
        }
    }

    fun getAppContact(
        mobileContact: ArrayList<UserModel>
    ): MutableList<UserModel>? {
        if(appContacts == null) {
            val phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            val query = databaseReference.orderByChild("number")
            appContacts = ArrayList<UserModel>()

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        result.children.forEach { snapshot ->
                            val number = snapshot.child("number").value.toString()
                            for (mobileModel in mobileContact) {
                                if (mobileModel.number == number && number != phoneNumber) {
                                    val userModel = snapshot.getValue(UserModel::class.java)
                                    if (userModel != null ) {
                                        if(userModel !in appContacts as ArrayList<UserModel>){
                                            appContacts?.add(userModel)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.d("ERROR", task.exception.toString())
                }
            }
        }
        return appContacts
    }

    fun getContactUID(UID: String?): UserModel {
            if (UID != null) {
                appUtil.getDatabaseReferenceUsers().child(UID)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                val userModel = snapshot.getValue(UserModel::class.java)
                                contactUserData = userModel!!
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("Error",error.toString())
                        }
                    })
            }

        return contactUserData
    }

    operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(values: MutableList<T>) {
        val value = this.value ?: mutableListOf()
        value.addAll(values)
        this.value = value
    }
}