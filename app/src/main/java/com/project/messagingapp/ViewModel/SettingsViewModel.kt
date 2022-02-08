package com.project.messagingapp.ViewModel

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.project.messagingapp.R
import com.project.messagingapp.model.SettingList
import kotlinx.android.synthetic.main.activity_user_registration_profile.*

class SettingsViewModel : ViewModel() {

    var id = ""
    var title=""
    var image = ""
    constructor():super()

    constructor(setting: SettingList) : super() {
        this.id = setting.id
        this.title = setting.title
        this.image = setting.image
    }

    var arrlistMutableLiveData = MutableLiveData<ArrayList<SettingsViewModel>>()

    var arrlist = ArrayList<SettingsViewModel>()


    fun getArrayList() : MutableLiveData<ArrayList<SettingsViewModel>>{
        val editprofile = SettingList("1","Edit Profile","test.png")
        val editprofile2 = SettingList("2","Edit Profile2","test3.png")
        val editprofile3 = SettingList("3","Edit Profile3","test4.png")

        val settingviewmodel1: SettingsViewModel = SettingsViewModel(editprofile)
        val settingviewmodel2: SettingsViewModel = SettingsViewModel(editprofile2)
        val settingviewmodel3: SettingsViewModel = SettingsViewModel(editprofile3)

        arrlist.add(settingviewmodel1)
        arrlist.add(settingviewmodel2)
        arrlist.add(settingviewmodel3)

        arrlistMutableLiveData.value = arrlist

        return arrlistMutableLiveData
    }

    fun getImageURL() : String{
     return image
    }
}

object IconBindingAdapter{
    @JvmStatic
    @BindingAdapter("android:src")

    fun setIconImage(view: ImageView, url: String){
        Glide.with(view.context).load(url).placeholder(R.drawable.ic_baseline_person_add_24).into(view)
    }
}

