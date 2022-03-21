package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.messagingapp.ui.main.view.fragments.SettingsFragment
import com.project.messagingapp.R
import com.project.messagingapp.utils.AppUtil

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.beginTransaction()
            .add(R.id.setting_container, SettingsFragment())
            .commit()

    }

//    override fun onPause() {
//        super.onPause()
//        AppUtil().updateOnlineStatus("offline")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        AppUtil().updateOnlineStatus("online")
//    }
}