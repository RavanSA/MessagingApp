package com.project.messagingapp.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.ui.main.view.fragments.SettingsFragment
import com.project.messagingapp.R
import com.project.messagingapp.ui.main.viewmodel.SettingsViewModel
import com.project.messagingapp.databinding.SettingListBinding
import com.project.messagingapp.ui.main.view.fragments.SettingRecyclerClickListener

class CustomSettingsAdapter(
    private val context: SettingsFragment,
    private val listener: SettingRecyclerClickListener,
    private val arrayList: ArrayList<SettingsViewModel>)
    :RecyclerView.Adapter<CustomSettingsAdapter.CustomSettingView>()

{


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomSettingsAdapter.CustomSettingView {

        val layoutinflater = LayoutInflater.from(parent.context)

        val settingView: SettingListBinding = DataBindingUtil.inflate(layoutinflater,
            R.layout.setting_recycler_view, parent,false)


        return CustomSettingView(settingView)

    }

    override fun onBindViewHolder(holder: CustomSettingsAdapter.CustomSettingView, position: Int)
    {
        holder.bindSettingList.settingsView = arrayList[position]
        Log.d("ARRAYLIST",arrayList[position].toString())
        holder.bindSettingList.clickLinear.setOnClickListener{
            listener.onRecyclerViewItemClick(holder.bindSettingList.clickLinear,arrayList[position])
        }

    }

    override fun getItemCount(): Int {
       return arrayList.size
    }

    inner class CustomSettingView(val bindSettingList: SettingListBinding)
        :RecyclerView.ViewHolder(bindSettingList.root)
}