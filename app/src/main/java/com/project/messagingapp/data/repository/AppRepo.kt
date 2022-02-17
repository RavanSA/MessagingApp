package com.project.messagingapp.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.project.messagingapp.constants.AppConstants
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.utils.AppUtil

class AppRepo {
    private var liveData: MutableLiveData<UserModel>? = null
    private var appUtil = AppUtil()
    private var userUploadData: MutableLiveData<FirebaseDatabase>? = null

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
            appUtil.getDatabaseReferenceUsers().child(appUtil.getUID()!!)
                .addValueEventListener(object : ValueEventListener {
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

     fun UploadData(username: String, status: String, image: Uri) {
         val currentUserReference = appUtil.getDatabaseReferenceUsers().
         child(appUtil.getUID()!!)

         userUploadData = MutableLiveData()
            appUtil.getUID()!!.let {
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
}