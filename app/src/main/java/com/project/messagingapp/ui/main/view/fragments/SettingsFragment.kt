package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.R
import com.project.messagingapp.ui.main.adapter.CustomSettingsAdapter
import com.project.messagingapp.ui.main.view.activities.UserProfile
import com.project.messagingapp.ui.main.view.activities.UserRegistrationProfile
import com.project.messagingapp.ui.main.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment(), SettingRecyclerClickListener {

    private lateinit var settingViewModel: SettingsViewModel
    private var settingRecyclerView: RecyclerView?= null
    private var customSettingAdapter: CustomSettingsAdapter ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
            .create(SettingsViewModel::class.java)

        settingRecyclerView = settingRecycler

        settingViewModel.getUserName().observe(viewLifecycleOwner, Observer { name ->
            tvSettingUserName.text = name.name
        })

        settingViewModel.getArrayList().observe(viewLifecycleOwner, { settingViewModels ->
            customSettingAdapter = CustomSettingsAdapter(this,this,settingViewModels!!)
            settingRecyclerView!!.layoutManager = LinearLayoutManager(activity)

            settingRecyclerView!!.adapter = customSettingAdapter
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onRecyclerViewItemClick(view: View, settingList: SettingsViewModel) {
          when(settingList.title){
            "Account" -> {

                startActivity(Intent(context, UserProfile::class.java))
                requireActivity().finish()
            }

            "Chat" -> {
                Toast.makeText(requireContext(),"CHAT", Toast.LENGTH_LONG).show()
            }
            "Help" -> {
                Toast.makeText(requireContext(),"HELP", Toast.LENGTH_LONG).show()
            }
        }
    }

}

interface SettingRecyclerClickListener{
    fun onRecyclerViewItemClick(view: View, settingList: SettingsViewModel)
}