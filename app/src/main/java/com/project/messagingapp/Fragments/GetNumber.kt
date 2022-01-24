package com.project.messagingapp.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.project.messagingapp.R
import com.project.messagingapp.UserModel
import kotlinx.android.synthetic.main.fragment_get_number.view.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KMutableProperty1


class GetNumber : Fragment() {
    private var number: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_get_number, container, false)
        view.otpSendBtn.setOnClickListener {
            if(checkNumber()){
                sendOneTimePassword(number)
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
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.main_container, GetUserData())
                            ?.commit()

                        val sharedPref: SharedPreferences = activity!!.applicationContext.getSharedPreferences("com.project.messaginapp.phonenumber",Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("PhoneNumber",number)
                        editor.apply()
                    }
                }
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                activity!!.supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.main_container, VerifyNum
                                .newInstance(p0)
                        ).commit()
            }
            override fun onVerificationFailed(p0: FirebaseException) {
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
        number = requireView().edtPhone.text.toString().trim()
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