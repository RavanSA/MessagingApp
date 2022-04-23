package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.project.messagingapp.ui.main.view.activities.UserRegistrationProfile
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.model.UserRoomModel
import com.project.messagingapp.ui.main.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_get_number.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class GetNumber : Fragment() {
    private var number: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        val act = activity?.application!!



        val progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("We sending code, please wait")
        val view = inflater.inflate(R.layout.fragment_get_number, container, false)

        registrationViewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        view.otpSendBtn.setOnClickListener {
            if(checkNumber()){
                sendOneTimePassword(number)
                progressDialog.show()
            }
        }

        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            @SuppressLint("CommitPrefEdits")
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                firebaseAuth!!.signInWithCredential(p0).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userModel =
                            UserModel(
                                "", "", "",
                                firebaseAuth!!.currentUser!!.phoneNumber!!,
                                firebaseAuth!!.uid!!
                            )

//                        val userRoom = UserRoomModel(firebaseAuth!!.uid!!,"test account"
//                            ,firebaseAuth!!.currentUser!!.phoneNumber!!,"sadfdsfsafsdfsdf")
//
//                        Log.d("RegistrationVIEWMODEL","CALLED")
//                        try{
//                            registrationViewModel.insertUser(userRoom)
//                        } catch (e: Exception){
//                            Log.d("EXCEPTIONOOCURED",e.toString())
//                        }

                        databaseReference!!.child(firebaseAuth!!.uid!!).setValue(userModel)
                        startActivity(Intent(context, UserRegistrationProfile::class.java))
                        requireActivity().finish()

                    }
                }
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                progressDialog.cancel()
                progressDialog.dismiss()
                activity!!.supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.main_container, VerifyNum
                                .newInstance(p0)
                        ).commit()
            }


            override fun onVerificationFailed(p0: FirebaseException) {
                progressDialog.cancel()
                progressDialog.dismiss()
                if(p0 is FirebaseAuthInvalidCredentialsException)
                    Toast.makeText(context, " " + p0.message, Toast.LENGTH_SHORT).show()
                else if (p0 is FirebaseTooManyRequestsException)
                    Toast.makeText(context, " " + p0.message, Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, " " + p0.message, Toast.LENGTH_SHORT).show()

            }

        }
        return view
    }

    private fun sendOneTimePassword(number: String?) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(number!!)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallBack!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun checkNumber(): Boolean {
        number =requireView().country_code_picker.selectedCountryCodeWithPlus+ requireView().edtPhone.text.toString().trim()
        Log.d("NUMBER:", number!!)
        return when {
            number!!.isEmpty() -> {
                requireView().edtPhone?.error ="Field is reqiured"
                false
            }
            number!!.length<10 -> {
                requireView().edtPhone?.error ="Invalid length"
                false
            }
            else -> {
                val sharedPref: SharedPreferences = context!!
                    .getSharedPreferences("com.project.messaginapp.phonenumber",
                        Context.MODE_PRIVATE)

                val editor = sharedPref.edit()
                number?.let { Log.d("PHONENUMBER,", it) }
                editor.putString("PhoneNumber",number?.let { it })
                editor.apply()
                true
            }
        }
    }

}