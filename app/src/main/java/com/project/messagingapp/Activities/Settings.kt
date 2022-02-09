package com.project.messagingapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.messagingapp.Fragments.SettingsFragment
import com.project.messagingapp.Fragments.WelcomeFragment
import com.project.messagingapp.R

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.beginTransaction()
            .add(R.id.setting_container, SettingsFragment())
            .commit()
    }
}