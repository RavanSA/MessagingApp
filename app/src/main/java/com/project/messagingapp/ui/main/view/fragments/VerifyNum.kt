package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        autoPassEditText()
//        resendButton()

    }

    @SuppressLint("SetTextI18n")
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

        val view = inflater.inflate(R.layout.fragment_verify_num, container, false)


        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        view.OTPPinSend.setOnClickListener {
            if (CheckPin()) {
                val credential = PhoneAuthProvider.getCredential(p0!!, pin!!)
                signInUser(credential)
            }
        }


        resendButton(view)

        view.resendCode.setOnClickListener {
            p0?.let { it1 -> GetNumber().callFromVerifyNum(it1) }
        }

        return view
    }

    private fun signInUser(credential: PhoneAuthCredential) {
        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val userModel = UserModel(
                    "", "", "", firebaseAuth!!.currentUser!!.phoneNumber!!,
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

    private fun CheckPin(): Boolean {
        pin = requireView().edt_one.text.toString() + requireView().edt_two.text.toString() +
                requireView().edt_three.text.toString() + requireView().edt_four.text.toString() +
                requireView().edt_five.text.toString() + requireView().edt_six.text.toString()
        if (pin!!.isEmpty()) {
            requireView().edt_three!!.error = "Field is required"
            return false
        } else if (pin!!.length < 6) {
            requireView().edt_three!!.error = "Invalid length"
            return false
        } else return true
    }

    private fun autoPassEditText() {

        requireView().edt_one.addTextChangedListener(
            GenericTextWatcher(
                requireView().edt_one,
                requireView().edt_two
            )
        )
        requireView().edt_two.addTextChangedListener(
            GenericTextWatcher(
                requireView().edt_two,
                requireView().edt_three
            )
        )
        requireView().edt_three.addTextChangedListener(
            GenericTextWatcher(
                requireView().edt_three,
                requireView().edt_four
            )
        )
        requireView().edt_four.addTextChangedListener(
            GenericTextWatcher(
                requireView().edt_four,
                requireView().edt_five
            )
        )
        requireView().edt_five.addTextChangedListener(
            GenericTextWatcher(
                requireView().edt_five,
                requireView().edt_six
            )
        )
        requireView().edt_six.addTextChangedListener(
            GenericTextWatcher(
                requireView().edt_six,
                null
            )
        )

        requireView().edt_one.setOnKeyListener(GenericKeyEvent(requireView().edt_one, null))
        requireView().edt_two.setOnKeyListener(
            GenericKeyEvent(
                requireView().edt_two,
                requireView().edt_one
            )
        )
        requireView().edt_three.setOnKeyListener(
            GenericKeyEvent(
                requireView().edt_three,
                requireView().edt_two
            )
        )
        requireView().edt_four.setOnKeyListener(
            GenericKeyEvent(
                requireView().edt_four,
                requireView().edt_three
            )
        )
        requireView().edt_five.setOnKeyListener(
            GenericKeyEvent(
                requireView().edt_five,
                requireView().edt_four
            )
        )
        requireView().edt_six.setOnKeyListener(
            GenericKeyEvent(
                requireView().edt_six,
                requireView().edt_five
            )
        )

    }

    private fun resendButton(view: View) {
        val timer = 59
        val timerText = view.resendCodeTimer
        view.resendCode.isEnabled = false
        while (timer == 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                timerText.text = "00:" + (timer - 1).toString()
                Log.d("TIMER", timer.toString())
            }, 1000)
            if (timer == 0) {
                view.resendCode.isEnabled = true
            }
            Log.d("TIMER", timer.toString())

        }
    }

    class GenericTextWatcher internal constructor(
        private val currentView: View,
        private val nextView: View?
    ) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
            val text = editable.toString()
            when (currentView.id) {
                R.id.edt_one -> if (text.length == 1) nextView!!.requestFocus()
                R.id.edt_two -> if (text.length == 1) nextView!!.requestFocus()
                R.id.edt_three -> if (text.length == 1) nextView!!.requestFocus()
                R.id.edt_four -> if (text.length == 1) nextView!!.requestFocus()
                R.id.edt_five -> if (text.length == 1) nextView!!.requestFocus()
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) { // TODO Auto-generated method stub
        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) { // TODO Auto-generated method stub
        }

    }
}