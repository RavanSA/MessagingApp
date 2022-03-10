package com.project.messagingapp.data.repository.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.project.messagingapp.constants.AppConstants
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.utils.AppUtil

class AppRepo {
    private var liveData: MutableLiveData<UserModel>? = null
    private var contactUserData: UserModel = UserModel()
    private var appContacts: MutableList<UserModel>? = null
    private var appUtil = AppUtil()
    private var userUploadData: MutableLiveData<FirebaseDatabase>? = null

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
                .addOnSuccessListener { it ->
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
    ): List<UserModel> {
        if(appContacts == null) {

            val phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            val query = databaseReference.orderByChild("number")
            appContacts = arrayListOf<UserModel>()

            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("SNAPSHOT1", snapshot.toString())
                    Log.d("APPCONTACT3", appContacts.toString())
                    if (snapshot.exists()) {
                        Log.d("PHONENUMBER", phoneNumber.toString())
                        for (data in snapshot.children) {
                            Log.d("APPCONTACT4", appContacts.toString())
                            val number = data.child("number").value.toString()
                            for (mobileModel in mobileContact) {
                                if (mobileModel.number == number && number != phoneNumber) {
                                    val userModel = data.getValue(UserModel::class.java)
                                    Log.d("APPCONTACT5", appContacts.toString())
                                    if (userModel != null) {
                                        (appContacts)!!.add(userModel)

                                    }
                                }
                            }
                        }
                        appContacts!!.removeAt(1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR", error.toString())
                }
            })
        }
        return appContacts!! as List<UserModel>
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
}