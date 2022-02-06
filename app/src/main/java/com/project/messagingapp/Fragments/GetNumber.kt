package com.project.messagingapp.Fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.project.messagingapp.Activities.UserRegistrationProfile
import com.project.messagingapp.R
import com.project.messagingapp.UserModel
import kotlinx.android.synthetic.main.fragment_get_number.*
import kotlinx.android.synthetic.main.fragment_get_number.view.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KMutableProperty1


class GetNumber : Fragment() {
    private var number: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var countryCode:String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        country_code_picker!!.setOnCountryChangeListener(this)
//        country_code_picker!!.setOnCountryChangeListener(this)
//        //to set default country code as Japan
//        countryCode = country_code_picker!!.selectedCountryCode
        val progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("We sending code, please wait")
        val view = inflater.inflate(R.layout.fragment_get_number, container, false)
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
                        databaseReference!!.child(firebaseAuth!!.uid!!).setValue(userModel)
                        startActivity(Intent(context, UserRegistrationProfile::class.java))
                        requireActivity().finish()

                        val sharedPref: SharedPreferences = activity!!.applicationContext.getSharedPreferences("com.project.messaginapp.phonenumber",Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("PhoneNumber",number)
                        editor.apply()
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
            .setPhoneNumber(number!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(mCallBack!!)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun checkNumber(): Boolean {
        number =requireView().country_code_picker.selectedCountryCodeWithPlus+ requireView().edtPhone.text.toString().trim()
        Log.d("NUMBER:", number!!)
        if(number!!.isEmpty()){
            requireView().edtPhone?.error ="Field is reqiured"
            return false
        } else if (number!!.length<10){
            requireView().edtPhone?.error ="Invalid length"
            return false
        } else
            return true
    }
}