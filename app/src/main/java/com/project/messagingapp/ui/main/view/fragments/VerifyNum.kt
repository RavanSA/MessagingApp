package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.messagingapp.ui.main.view.activities.UserRegistrationProfile
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import kotlinx.android.synthetic.main.fragment_verify_num.view.*

class VerifyNum : Fragment() {
    private var p0: String? = null
    private var pin: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
                p0 = it.getString("Code")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_verify_num, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        view.OTPPinSend.setOnClickListener {
            if(CheckPin()){
                val credential = PhoneAuthProvider.getCredential(p0!!,pin!!)
                signInUser(credential)
            }
        }

        return view
    }

    private fun signInUser(credential: PhoneAuthCredential) {
        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                val userModel = UserModel(
                    "","","",firebaseAuth!!.currentUser!!.phoneNumber!!,
                    firebaseAuth!!.uid!!
                )
                databaseReference!!.child(firebaseAuth?.uid!!).setValue(userModel)

                startActivity(Intent(context, UserRegistrationProfile::class.java))
                requireActivity().finish()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(p0: String) =
            VerifyNum().apply {
                arguments = Bundle().apply {
                        putString("Code", p0)
                 }
            }
    }

    private fun CheckPin():Boolean {
        pin = requireView().OTPPinText!!.text.toString()
        if(pin!!.isEmpty()){
            requireView().OTPPinText!!.error = "Field is required"
            return false
        } else if (pin!!.length < 6){
            requireView().OTPPinText!!.error = "Invalid length"
            return false
        }else return true
    }
}