package com.project.messagingapp.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val PERMISSION_REQ_CODE = 114

class AppUtil {


    fun getUID () : String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
    }

    fun getStorageReference(): StorageReference {
        val storageRef = FirebaseStorage.getInstance().reference
        return storageRef
    }

    fun getDatabaseReferenceUsers(): DatabaseReference{
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Users")
        return databaseReference
    }

    fun getChatQuery(conversationID: String): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("Chat")
            .child(conversationID)
    }

    fun updateOnlineStatus( status: String){
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Users").child(getUID()!!)
        val map = HashMap<String, Any>()
        map["online"] = status
        databaseRef.updateChildren(map)
    }

    fun updateUserLocation(log: String, lat: String){
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Users").child(getUID()!!)
        val map = HashMap<String, Any>()
        map["locationLatitude"] =lat
        map["locationLongtitude"] =log
        databaseRef.updateChildren(map)
    }

    fun getTimeAgo(date: Long): String{
        return date.toString()
    }

    fun checkInternetConnection(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun checkPermission(context: Activity,
                        vararg permissions: String, reqCode: Int= PERMISSION_REQ_CODE): Boolean {
        var allPermitted = false
        for (permission in permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED)
            if (!allPermitted) break
        }
        if (allPermitted) return true
        context.requestPermissions(
            permissions,
            reqCode
        )
        return false
    }

}