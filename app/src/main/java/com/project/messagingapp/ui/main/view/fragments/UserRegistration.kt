package com.project.messagingapp.ui.main.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.project.messagingapp.R
import com.project.messagingapp.ui.main.viewmodel.ProfileViewModel
import com.project.messagingapp.ui.main.viewmodel.UserRegistrationViewModel


class UserRegistration : Fragment() {



    private lateinit var userViewModel: UserRegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
            .create(UserRegistrationViewModel::class.java)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_registration, container, false)
    }
}