package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import kotlinx.android.synthetic.main.fragment_verify_num.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken


class GetNumber : Fragment() {
    private var number: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mResendToken: ForceResendingToken? = null

    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
                firebaseAuth?.signInWithCredential(p0)?.addOnCompleteListener {
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

                mResendToken = p1
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDefaultCountry()
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

    fun resendVerificationCode(
        phoneNumber: String,
        token: ForceResendingToken
    ) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallBack!!)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun callFromVerifyNum(phoneNumber: String){
        mResendToken?.let { resendVerificationCode(phoneNumber, it) }
    }

    private fun setDefaultCountry() {
            val manager =
                requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)?
            Log.d("MANAGER", manager?.networkCountryIso.toString())
        val countryCode: String? = manager?.networkCountryIso
        requireView().country_code_picker.setDefaultCountryUsingNameCode(countryCode)
        requireView().country_code_picker.resetToDefaultCountry()
    }
}

class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.edt_one && currentView.text.isEmpty()) {
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }

}
