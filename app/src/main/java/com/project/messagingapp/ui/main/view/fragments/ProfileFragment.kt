package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.project.messagingapp.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.ui.main.viewmodel.ProfileViewModel
import com.project.messagingapp.databinding.FragmentProfileBinding
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.dialog_layout.view.*


class ProfileFragment : Fragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dialog:AlertDialog

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        profileBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,
            container,false)

        profileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
            .create(ProfileViewModel::class.java)


        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { userModel ->
            profileBinding.userModel = userModel
                ProfileFirstName.text ="Firstname: " + userModel.name
                ProfilePhone.text = "Phone: " + userModel.number.toString()
                ProfileStatus.text = "Status: " + userModel.status
        })
        profileBinding.ProfileStatus.setOnClickListener {
            Toast.makeText(context,"WORKED",Toast.LENGTH_LONG).show()
            updateAlertDialog()
        }
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    private fun updateAlertDialog(){
        val updateDialog = AlertDialog.Builder(context)
        val layout:View = LayoutInflater.from(context).inflate(R.layout.dialog_layout,
            null,false)
        updateDialog.setView(layout)

        requireView().btnEdit.setOnClickListener {
            val status = requireView().updateDialog.text.toString()
            if(!status.isEmpty()){
                profileViewModel.updateStatus(status)
                dialog.dismiss()
            }
        }
        dialog = updateDialog.create()
        dialog.show()
    }
}