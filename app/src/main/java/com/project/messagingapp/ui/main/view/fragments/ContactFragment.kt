package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelProviders.of
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.databinding.FragmentContactBinding
import com.project.messagingapp.ui.main.adapter.CustomContactAdapter
import com.project.messagingapp.ui.main.viewmodel.ContactViewModel
import com.project.messagingapp.ui.main.viewmodel.ContactViewModelFactory
import com.project.messagingapp.ui.main.viewmodel.ProfileViewModel
import com.project.messagingapp.utils.ContactPermission

class ContactFragment : Fragment() {
    private lateinit var contactBinding: FragmentContactBinding
    private lateinit var phoneNumber: String
    private var contactAdapter: CustomContactAdapter? = null
    private lateinit var contactViewModel: ContactViewModel
    private var contactPermission: ContactPermission = ContactPermission()
    private lateinit var mobileContact: ArrayList<UserModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contactBinding = FragmentContactBinding.inflate(inflater,container,false)

        if(contactPermission.isContactOk(requireContext())) {
            Log.d("VIEWMODEL","NOT OBSERVED YET")
            Log.d("VIEWMODEL","OBSERVED")
            mobileContact = getMobileContact()

            contactViewModel = ViewModelProvider(this,
                ContactViewModelFactory(getMobileContact()))[ContactViewModel::class.java]
            contactBinding.lifecycleOwner = this

        }   else contactPermission.requestContactPermission(requireActivity())

        return contactBinding.root
    }

//    private fun observeContactData() {
//        Log.d("MOBILE CONTACT",mobileContact.toString())
//        contactViewModel.appContact().observe(this, { userModel ->
//            Log.d("NAMEAPPCONTACT", "USER: $userModel")
//            val name: String = userModel.toString()
////            contactBinding.
//            Log.d("NAMEAPPCONTACT", "USER: $name")
//            //            Log.d("NAMEAPPCONTACT", data.name.toString())
//            lateinit var arrList: ArrayList<UserModel>
//
//
//            contactBinding.recyclerViewContact.apply {
//                layoutManager = LinearLayoutManager(context)
//                setHasFixedSize(true)
//                contactAdapter = CustomContactAdapter(userModel)
//                adapter = contactAdapter
//            }
////            data?.let {
////                it.also { arrList.add() }
////                Log.d("arraylistContact", arrList.toString())
////            }
//
//        })
//    }

    @SuppressLint("Range")
    private fun getMobileContact(): ArrayList<UserModel> {
        lateinit var mobileContacts: ArrayList<UserModel>

        val projection = arrayOf(
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val contentResolver = context!!.contentResolver
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
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .Phone.DISPLAY_NAME))
                var number =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
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


//            contactBinding.recyclerViewContact.apply {
//                layoutManager = LinearLayoutManager(context)
//                setHasFixedSize(true)
//                contactAdapter = CustomContactAdapter(contactViewModel.appContact(mobileContacts)
//                )
//                adapter = contactAdapter
//            }
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
                else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}