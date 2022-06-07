package com.project.messagingapp.data.repository.remote

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.project.messagingapp.constants.AppConstants
import com.project.messagingapp.data.daos.ContactListDao
import com.project.messagingapp.data.model.*
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import com.google.firebase.database.DataSnapshot




class AppRepo(
    private val contactListDao: ContactListDao
) {
    private var liveData: MutableLiveData<UserModel>? = null
    private var contactUserData: UserModel = UserModel()
    private var appContacts: MutableList<UserModel>? = null
    private var appUtil = AppUtil()
    private var userUploadData: MutableLiveData<FirebaseDatabase>? = null
    private var messages: MutableList<MessageModel>? = null

    object SingletonStatic{
        private var instance: AppRepo? = null

            fun getInstance(contactListDao: ContactListDao): AppRepo {
                if(instance == null)
                    instance = AppRepo(contactListDao)
                return instance!!
            }

    }

//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
//     fun getContactListRoom(): Flow<MutableList<ContactChatList>> {
//        return contactListDao.getAllContactList()
//    }

//    fun getContactListAndChatList(): List<ContactListandChatList>{
//        return contactListDao.getContactListAndChatList()
//    }

    fun getContactList(): List<ContactListRoom>{
        return contactListDao.getContactList()
    }

      fun addReceiverInformation(contactListRoom: ContactListRoom){
          Log.d("ADDED TO DATABASE", contactListRoom.toString())
        contactListDao.addReceiverInformation(contactListRoom)
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
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("Users").child(AppUtil().getUID()!!)
        Log.d("UPLOADİNG","PICTURE")
        try {


            AppUtil().getUID()!!.let { it ->
                FirebaseStorage.getInstance().getReference().child(AppConstants.Path).child(it)
                    .putFile(imageURI!!)
                    .addOnSuccessListener {
                        val task = it.storage.downloadUrl
                        task.addOnCompleteListener { uri ->
                            Log.d("FIREBASEUPLOAD", uri.toString())
                            val imageUrl = uri.result.toString()
                            Log.d("FIREBASDEIMAGEURL",imageUrl)
                            val map = mapOf(
                                "image" to imageUrl
                            )
                            databaseRef.updateChildren(map)
                            Log.d("MAP", map.toString())
                        }
                    }
            }
        } catch (e:Exception){
            Log.d("UPLOADEXCEPTION",e.toString())
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
                                            val contactListRoom = ContactListRoom(userModel.uid!!, userModel.name!!,userModel.number!!,
                                                userModel.status!!,userModel.image!!)
                                                Log.d("CONTACTLIST", contactListRoom.toString())
                                                addReceiverInformation(contactListRoom)

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

    fun findNearbyUsersUsingHarvesineDistance(): MutableLiveData<MutableList<UserModel>>{
        val nearbyUsersList = mutableListOf<UserModel>()
        val nearbyUsersLiveData: MutableLiveData<MutableList<UserModel>> = MutableLiveData()
        val currentUserLocation = getCurrentUserLocation()

        appUtil.getDatabaseReferenceUsers().addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val uid = ds.child("uid").value.toString()
                    if ( uid != AppUtil().getUID()) {
                        val userLocationLantitude = ds.child("locationLatitude").value.toString()
                        val userLocationLongtitude = ds.child("locationLongtitude").value.toString()
                        Log.d("USERLOCATİONLAT", userLocationLantitude)
                        Log.d("USERLOCATİONLONG", userLocationLongtitude)

                        val distanceCalculator: Double = haversineDistance(
                            currentUserLocation[0],
                            currentUserLocation[1],
                            userLocationLantitude, userLocationLongtitude
                        )

                        if (distanceCalculator <= 10) {
                            Log.d("SNAPASHOTTEST", ds.toString())
                            val userModel = ds.getValue(UserModel::class.java)
                            userModel?.let { nearbyUsersList.add(it) }
                        }
                        Log.d("USERLIST", nearbyUsersList.toString())
                    }
                }
                nearbyUsersLiveData.value = nearbyUsersList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR OCCURED: ", error.toString())
            }

        })
        Log.d("NEARBYUSERLIST", nearbyUsersList.toString())
        return nearbyUsersLiveData
    }

    private fun strToDouble(str: String): Double {
        if(str == "" || str == "null"){
            return 0.00
        }
        return str.toDouble()
    }

    fun haversineDistance(latCurrentUser: String, longCurrentUser: String,
                            latUser: String, longUser: String): Double {

        val earthRadiusKm: Double = 6372.8
        val latCurrentUserDouble = strToDouble(latCurrentUser)
        val longCurrentUserDouble = strToDouble(longCurrentUser)
        val latUserDouble = strToDouble(latUser)
        val longUserDouble = strToDouble(longUser)
        val dLat = Math.toRadians(latCurrentUserDouble - latUserDouble);
        val dLon = Math.toRadians(longCurrentUserDouble - longUserDouble);
        Log.d("LATCURRENTUSER", latCurrentUserDouble.toString())
        Log.d("LongCURRENTUSER", longCurrentUserDouble.toString())
        Log.d("LATUSER", latUserDouble.toString())
        Log.d("LongUSER", longUserDouble.toString())
//        val originLat = Math.toRadians(this.lat);
//        val destinationLat = Math.toRadians(destination.lat);

        val a = Math.pow(Math.sin(dLat / 2), 2.toDouble()) + Math.pow(Math.sin(dLon / 2), 2.toDouble()) * Math.cos(latCurrentUserDouble) * Math.cos(latUserDouble);
        val c = 2 * Math.asin(Math.sqrt(a));
        Log.d("CALCULATINGHAVERSINE", (earthRadiusKm * c).toString())
        return earthRadiusKm * c;
    }

    fun getCurrentUserLocation(): Array<String> {

        val gps = arrayOf("","")

        appUtil.getDatabaseReferenceUsers().child(appUtil.getUID()!!).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("NAPSHOTTRST", snapshot.toString())
                        val currentUserLocationLatitude =  snapshot.child("locationLatitude").getValue(String::class.java)

                        val currentUserLocationLongtitutde =  snapshot.child("locationLongtitude").getValue(String::class.java)
                        if (currentUserLocationLatitude != null) {
                            gps[0] = currentUserLocationLatitude
                        }
                        if (currentUserLocationLongtitutde != null) {
                            gps[1] = currentUserLocationLongtitutde
                        }

                        Log.d("CURRENTUSERLOCATİONLAT", currentUserLocationLatitude.toString())
                        Log.d("CURRENTUSERLOCATİONLog", currentUserLocationLongtitutde.toString())

                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR OCCURED", error.toString())
                }

            })
        return gps
    }


    fun getEmotion(): String {
        var emotion: String = ""
        appUtil.getDatabaseReferenceUsers().child(appUtil.getUID()!!).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("NAPSHOTTRST", snapshot.toString())
                    emotion = snapshot.child("emotion").getValue(String::class.java).toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR OCCURED", error.toString())
                }
            })
        return emotion
    }

}