package com.project.messagingapp.Fragments

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat
import androidx.core.content.FileProvider
import com.project.messagingapp.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.messagingapp.BuildConfig
import com.project.messagingapp.ViewModel.ProfileViewModel
import com.project.messagingapp.databinding.FragmentProfileBinding
import kotlinx.android.synthetic.main.activity_user_registration_profile.*
import kotlinx.android.synthetic.main.fragment_get_number.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File

//import com.project.messagingapp.ViewModel.ProfileViewModel as ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        profileBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)

        profileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
            .create(ProfileViewModel::class.java)


        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { userModel ->
            profileBinding.userModel = userModel

            if(userModel.name?.contains(" ") == true)  {
                val split = userModel.name.split(" ")

                profileBinding.ProfileFirstName.text = split[0]
                profileBinding.ProfileLastName.text = split[1]
                ProfileFirstName.text =split[0]
                ProfileLastName.text =split[1]
                ProfilePhoneNumber.text = userModel.number.toString()
                Log.d("FIRSTNAME:", split[0])
                Log.d("LASTNAME:", split[1])
                Log.d("PHONE:", userModel.number.toString())
            }
        })
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

}