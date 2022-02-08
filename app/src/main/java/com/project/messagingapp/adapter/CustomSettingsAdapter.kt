package com.project.messagingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.Fragments.SettingsFragment
import com.project.messagingapp.R
import com.project.messagingapp.ViewModel.SettingsViewModel
import com.project.messagingapp.databinding.SettingListBinding

class CustomSettingsAdapter(private val context: SettingsFragment,
                            private val arrayList: ArrayList<SettingsViewModel>):RecyclerView.Adapter<CustomSettingsAdapter.CustomSettingView>()
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

    override fun onBindViewHolder(holder: CustomSettingsAdapter.CustomSettingView, position: Int) {
        val settingViewModel = arrayList[position]

        holder.bindSettingList(settingViewModel)
    }

    override fun getItemCount(): Int {
       return arrayList.size
    }




    class CustomSettingView(val settingView: SettingListBinding): RecyclerView.ViewHolder(settingView.root){
        fun bindSettingList(settingViewModel: SettingsViewModel){
            this.settingView.settingsView = settingViewModel
            settingView.executePendingBindings()
        }
    }
}