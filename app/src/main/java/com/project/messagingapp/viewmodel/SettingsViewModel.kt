package com.project.messagingapp.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.project.messagingapp.R
import com.project.messagingapp.model.SettingList

class SettingsViewModel : ViewModel {

    var id = ""
    var title=""
    var imageSettings = ""
    constructor():super()

    constructor(setting: SettingList) : super() {
        this.id = setting.id
        this.title = setting.title
        this.imageSettings = setting.imageSettings
    }

    var arrlistMutableLiveData = MutableLiveData<ArrayList<SettingsViewModel>>()

    var arrlist = ArrayList<SettingsViewModel>()


    fun getArrayList() : MutableLiveData<ArrayList<SettingsViewModel>>{
        val editprofile = SettingList("1","Edit Profile1","logo_png")
        val editprofile2 = SettingList("2","Edit Profile2","logo_png.png")
        val editprofile3 = SettingList("3","Edit Profile3","R.drawable.logo_png")

        val settingviewmodel1: SettingsViewModel = SettingsViewModel(editprofile)
        val settingviewmodel2: SettingsViewModel = SettingsViewModel(editprofile2)
        val settingviewmodel3: SettingsViewModel = SettingsViewModel(editprofile3)

        arrlist.add(settingviewmodel1)
        arrlist.add(settingviewmodel2)
        arrlist.add(settingviewmodel3)

        arrlistMutableLiveData.value = arrlist

        return arrlistMutableLiveData
    }

    fun getImageUrl() : String{
     return imageSettings
    }

}



object ImageBindingAdapter{
    @JvmStatic
    @BindingAdapter("imageURL")
    fun setImageURL(view: ImageView, url: String){
//        Glide.with(view.context).load(url).placeholder(R.drawable.ic_baseline_person_add_24).into(view)
        Log.d("IMAGERESOURCES",url)
        Glide.with(view.context)
            .load(view.context.resources
                .getIdentifier(url, "drawable", view.context.packageName))
                .into(view)
    }


}