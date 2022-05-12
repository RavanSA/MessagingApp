package com.project.messagingapp.ui.main.view.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.databinding.ActivityUserContactsBinding
import com.project.messagingapp.ui.main.adapter.CustomContactAdapter
import com.project.messagingapp.ui.main.viewmodel.ContactViewModel
import com.project.messagingapp.ui.main.viewmodel.ContactViewModelFactory
import com.project.messagingapp.utils.AppUtil
import com.project.messagingapp.utils.ContactPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//TODO("Dealing with data state")
//TODO("whether the data is currently loading, has loaded successfully or failed.")
class UserContacts : AppCompatActivity() {
    private lateinit var contactBinding: ActivityUserContactsBinding
    private lateinit var phoneNumber: String
    private var contactAdapter: CustomContactAdapter? = null
    private lateinit var contactViewModel:ContactViewModel
    private var contactPermission: ContactPermission = ContactPermission()
    private lateinit var mobileContact: ArrayList<UserModel>
    private var internetConnection: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactBinding = ActivityUserContactsBinding.inflate(layoutInflater)

        setContentView(contactBinding.root)

        internetConnection = AppUtil().checkInternetConnection(this)

        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(getMobileContact(), application)
        )[ContactViewModel::class.java]

        val newRoomListDB = mutableListOf<UserModel>()

        if(internetConnection) {
            if (contactPermission.isContactOk(this)) {
                mobileContact = getMobileContact()

                loadUsers()
            } else {
                contactPermission.requestContactPermission(this)
            }
        } else {
            val roomDbContactList = contactViewModel.getContactListRoom2()
            for(element in roomDbContactList){
                val user = UserModel(
                    element.name,
                    element.status,
                    element.image,
                    element.number,
                    element.receiverID,
                    "offline",
                    "false",
                    ""
                )
                newRoomListDB.add(user)
            }
            localLoadUser(newRoomListDB)
        }

        contactBinding.swipeContact.setOnRefreshListener {

            if(internetConnection) {
                loadUsers()
            } else{
                localLoadUser(newRoomListDB)
            }
            contactBinding.swipeContact.isRefreshing = false

        }

        searchingContact(
            contactBinding.contactSearchView
        )

    }

    private fun searchingContact(search: SearchView) {
        search.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                if(contactAdapter != null) {
                    contactAdapter!!.filter.filter(newText)
                    contactBinding.recyclerViewContact.adapter = contactAdapter
                    contactAdapter!!.notifyDataSetChanged()
                }
                return false
            }
    })

    }

    @SuppressLint("Range")
    private fun getMobileContact(): ArrayList<UserModel> {
        lateinit var mobileContacts: ArrayList<UserModel>

        val projection = arrayOf(
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val contentResolver = this.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        if (cursor != null) {
            mobileContacts = ArrayList()
            while (cursor.moveToNext()) {

                val name =
                    cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds
                        .Phone.DISPLAY_NAME))
                var number =
                    cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds
                        .Phone.NUMBER))

                number = number.replace("\\s".toRegex(), "")
                val num = number.elementAt(0).toString()
                if (num == "0")
                    number = number.replaceFirst("(?:0)+".toRegex(), "+92")
                val userModel = UserModel()
                userModel.name = name
                userModel.number = number
                mobileContacts.add(userModel)
            }

            cursor.close()

        }
        return mobileContacts
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadUsers(){
            contactViewModel.appContact().observe(this@UserContacts, Observer { data ->
                Log.d("ACTIVITYDATA", data.toString())
                contactBinding.recyclerViewContact.layoutManager =
                    LinearLayoutManager(this@UserContacts, LinearLayoutManager.VERTICAL, false)
                contactAdapter = data?.let { CustomContactAdapter(it) }
                contactBinding.recyclerViewContact.adapter = contactAdapter
                contactAdapter!!.notifyDataSetChanged()
            })

    }

    fun localLoadUser(users: MutableList<UserModel>){
        contactBinding.recyclerViewContact.layoutManager =
            LinearLayoutManager(this@UserContacts, LinearLayoutManager.VERTICAL, false)
        contactAdapter = users?.let { CustomContactAdapter(it) }
        contactBinding.recyclerViewContact.adapter = contactAdapter
        contactAdapter!!.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2000 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getMobileContact()
                else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        AppUtil().updateOnlineStatus("online")
    }

    override fun onPause() {
        super.onPause()
        AppUtil().updateOnlineStatus("offline")
    }

}