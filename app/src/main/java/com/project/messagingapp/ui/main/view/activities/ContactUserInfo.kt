package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.databinding.ActivityContactUserInfoBinding
import com.project.messagingapp.ui.main.viewmodel.ContactInfoViewModel
import kotlinx.android.synthetic.main.activity_contact_user_info.*

class ContactUserInfo : AppCompatActivity() {
    private lateinit var contactUserInfoBinding: ActivityContactUserInfoBinding
    private var UID: String? = null
    private lateinit var ctUserViewModel: ContactInfoViewModel
    private var reload = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactUserInfoBinding = ActivityContactUserInfoBinding.inflate(layoutInflater)
        setContentView(contactUserInfoBinding.root)

        ctUserViewModel = ViewModelProvider(this)[ContactInfoViewModel::class.java]
        UID = intent.getStringExtra("UID")
        Log.d("testUID", UID.toString())



         ctUserViewModel.getContactUID(UID).observe(this,androidx.lifecycle.Observer { data ->
             Log.d("testUIDinOBSERVE", UID.toString())
             if(data == null){
                 loadContactUserInfo()
             }else contactUserInfoBinding.userModel= data
            Log.d("testData",data.toString())
        })
    }

    private fun loadContactUserInfo() {
        ctUserViewModel.getContactUID(UID).observe(this,androidx.lifecycle.Observer { data ->
            if(data == null){
                loadContactUserInfo()
            }
            contactUserInfoBinding.userModel= data
        })
    }
}