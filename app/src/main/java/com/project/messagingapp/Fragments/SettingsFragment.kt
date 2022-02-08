package com.project.messagingapp.Fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.R
import com.project.messagingapp.ViewModel.SettingsViewModel
import com.project.messagingapp.adapter.CustomSettingsAdapter

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var settingViewModel: SettingsViewModel
    private var settingRecyclerView: RecyclerView?= null
    private var customSettingAdapter: CustomSettingsAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        settingViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
            .create(SettingsViewModel::class.java)

        settingViewModel.getArrayList().observe(this, Observer { settingViewModels ->
            customSettingAdapter = CustomSettingsAdapter(this@SettingsFragment,settingViewModels!! )
            settingRecyclerView!!.setLayoutManager(LinearLayoutManager(this@SettingsFragment))
            settingRecyclerView!!.setAdapter(customSettingAdapter)
        })

        return inflater.inflate(R.layout.settings_fragment, container, false)
    }


}