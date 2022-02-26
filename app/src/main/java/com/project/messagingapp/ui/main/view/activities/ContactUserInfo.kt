package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.databinding.ActivityContactUserInfoBinding
import com.project.messagingapp.ui.main.viewmodel.ContactInfoViewModel
import com.project.messagingapp.ui.main.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ContactUserInfo : AppCompatActivity() {
    private lateinit var contactUserInfoBinding: ActivityContactUserInfoBinding
    private var UID: String? = null
    private lateinit var ctUserViewModel: ContactInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactUserInfoBinding = ActivityContactUserInfoBinding.inflate(layoutInflater)
        setContentView(contactUserInfoBinding.root)

        ctUserViewModel = ViewModelProvider(this)[ContactInfoViewModel::class.java]
        UID = intent.getStringExtra("UID")
        Log.d("testUID", UID.toString())

        UID?.let { ctUserViewModel.getContactUID(it) }!!.observe(this, { data ->
            contactUserInfoBinding.userModel= data
            Log.d("testData",data.toString())
        })
    }
}