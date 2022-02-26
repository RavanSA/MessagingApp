package com.project.messagingapp.ui.main.view.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.databinding.ActivityUserContactsBinding
import com.project.messagingapp.databinding.FragmentContactBinding
import com.project.messagingapp.ui.main.adapter.CustomContactAdapter
import com.project.messagingapp.ui.main.viewmodel.ContactViewModel
import com.project.messagingapp.ui.main.viewmodel.ContactViewModelFactory
import com.project.messagingapp.utils.ContactPermission
import kotlinx.android.synthetic.main.contact_item.*

class UserContacts : AppCompatActivity() {
    private lateinit var contactBinding: ActivityUserContactsBinding
    private lateinit var phoneNumber: String
    private var contactAdapter: CustomContactAdapter? = null
    private lateinit var contactViewModel: ContactViewModel
    private var contactPermission: ContactPermission = ContactPermission()
    private lateinit var mobileContact: ArrayList<UserModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactBinding = ActivityUserContactsBinding.inflate(layoutInflater)

        setContentView(contactBinding.root)
        if(contactPermission.isContactOk(this)) {
            mobileContact = getMobileContact()

            contactViewModel = ViewModelProvider(this,
                ContactViewModelFactory(getMobileContact())
            )[ContactViewModel::class.java]
            contactBinding.lifecycleOwner = this
            contactBinding.contactViewModel=contactViewModel
//            txtContactName.text= contactViewModel.data.
//                contactBinding.recyclerViewContact.apply {
//                layoutManager = LinearLayoutManager(context)
//                setHasFixedSize(true)
//                contactAdapter = CustomContactAdapter()
//                adapter = contactAdapter
//            }
            Log.d("TESTTT",contactViewModel.data.toString())
        }   else contactPermission.requestContactPermission(this)

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
}