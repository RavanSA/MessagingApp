package com.project.messagingapp.ui.main.viewmodel

import android.app.Application
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.project.messagingapp.data.ChatDatabase
import com.project.messagingapp.data.model.SettingList
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.data.repository.remote.AppRepo
import com.project.messagingapp.ui.main.adapter.CustomSettingsAdapter

class SettingsViewModel : ViewModel {

    var id = ""
    var title=""
    var imageSettings = ""
    var settingDesc = ""

    constructor():super()

    constructor(setting: SettingList) : super() {
        this.id = setting.id
        this.title = setting.title
        this.imageSettings = setting.imageSettings
        this.settingDesc = setting.settingDesc
    }

    var arrlistMutableLiveData = MutableLiveData<ArrayList<SettingsViewModel>>()

    var arrlist: ArrayList<SettingsViewModel> = ArrayList<SettingsViewModel>()

    fun getArrayList() : MutableLiveData<ArrayList<SettingsViewModel>>{
        val editprofile = SettingList("1","Account","ic_baseline_manage_accounts_24","Privacy, Account,Settings")
        val editprofile2 = SettingList("2","Chat","ic_baseline_chat_24","Theme,wallpaper,history")
        val editprofile3 = SettingList("3","Help","ic_baseline_help_outline_24","terms&conditions, help, feedback")
        val editprofile4 = SettingList("4","Block List","ic_baseline_block_24","Remove block people you have blocked")

        val settingviewmodel1: SettingsViewModel = SettingsViewModel(editprofile)
        val settingviewmodel2: SettingsViewModel = SettingsViewModel(editprofile2)
        val settingviewmodel3: SettingsViewModel = SettingsViewModel(editprofile3)
        val settingviewmodel4: SettingsViewModel = SettingsViewModel(editprofile4)

        arrlist.add(settingviewmodel1)
        arrlist.add(settingviewmodel2)
        arrlist.add(settingviewmodel3)
        arrlist.add(settingviewmodel4)

        arrlistMutableLiveData.value = arrlist

        return arrlistMutableLiveData
    }

}

object ImageBindingAdapter{
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageURL(view: ImageView, url: String){
        Log.d("IMAGERESOURCES",url)
        Glide.with(view.context)
            .load(view.context.resources
                .getIdentifier(url, "drawable", view.context.packageName))
                .into(view)
    }
}