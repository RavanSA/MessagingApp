package com.project.messagingapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.project.messagingapp.constants.AppConstants

class ContactPermission {

    fun isStorageOk(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestStoragePermission(activity: Activity) {

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1000
        )
    }

    fun isContactOk(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestContactPermission(activity: Activity) {

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_CONTACTS),
            2000
        )
    }

}