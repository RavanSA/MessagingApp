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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.databinding.FragmentContactBinding
import com.project.messagingapp.ui.main.adapter.CustomContactAdapter
import com.project.messagingapp.ui.main.viewmodel.ContactViewModel
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
    ): View? {

        contactBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact,
            container, false)

        contactViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(ContactViewModel::class.java)


        if(contactPermission.isContactOk(requireContext())) {
            mobileContact = getMobileContact()
            Log.d("VIEWMODEL","NOT OBSERVED YET")
            observeContactData()
            Log.d("VIEWMODEL","OBSERVED")
//            contactBinding.recyclerViewContact.apply {
//                layoutManager = LinearLayoutManager(context)
//                setHasFixedSize(true)
//                contactAdapter = CustomContactAdapter(arrList)
//                adapter = contactAdapter
//            }
    }else contactPermission.requestContactPermission(requireActivity())

        return contactBinding.root
    }

    private fun observeContactData() {
        Log.d("MOBILE CONTACT",mobileContact.toString())
        contactViewModel.appContact(mobileContact).observe(this, { data ->
            Log.d("NAMEAPPCONTACT", "USER: $data")
            val name: String = data.toString()
//            contactBinding.
            Log.d("NAMEAPPCONTACT", "USER: $data")
            Log.d("NAMEAPPCONTACT", "USER: $data")
            //            Log.d("NAMEAPPCONTACT", data.name.toString())
            lateinit var arrList: ArrayList<UserModel>


//            data?.let {
//                it.also { arrList.add() }
//                Log.d("arraylistContact", arrList.toString())
//            }

        })
    }

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