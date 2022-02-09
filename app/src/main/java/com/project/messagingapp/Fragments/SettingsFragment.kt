package com.project.messagingapp.Fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.R
import com.project.messagingapp.viewmodel.SettingsViewModel
import com.project.messagingapp.adapter.CustomSettingsAdapter
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment() {

    private lateinit var settingViewModel: SettingsViewModel
    private var settingRecyclerView: RecyclerView?= null
    private var customSettingAdapter: CustomSettingsAdapter ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
            .create(SettingsViewModel::class.java)

        settingRecyclerView = settingRecycler

        settingViewModel.getArrayList().observe(this, Observer { settingViewModels ->
            customSettingAdapter = CustomSettingsAdapter(this,settingViewModels!! )
            settingRecyclerView!!.layoutManager = LinearLayoutManager(activity)
//            settingRecyclerView!!.setLayoutManager(LinearLayoutManager(this@SettingsFragment))
//            settingRecyclerView!!.setAdapter(customSettingAdapter)
            settingRecyclerView!!.adapter = customSettingAdapter
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        settingViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
//            .create(SettingsViewModel::class.java)
//
//
//        settingViewModel.getArrayList().observe(this, Observer { settingViewModels ->
//            customSettingAdapter = CustomSettingsAdapter(this,settingViewModels!! )
//            settingRecyclerView!!.layoutManager = LinearLayoutManager(context!!)
////            settingRecyclerView!!.setLayoutManager(LinearLayoutManager(this@SettingsFragment))
////            settingRecyclerView!!.setAdapter(customSettingAdapter)
//            settingRecyclerView!!.adapter = customSettingAdapter
//        })

        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

}