package com.project.messagingapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.messagingapp.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.ViewModel.ProfileViewModel
import com.project.messagingapp.databinding.FragmentProfileBinding
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


        profileViewModel.getUser().observe(viewLifecycleOwner, Observer {
            profileBinding.userModel = it

            if(it.name.contains(" ")){
                val split = it.name.split(" ")

                profileBinding.ProfileFirstName.text = split[0]
                profileBinding.ProfileLastName.text = split[1]
            }
        })
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

}