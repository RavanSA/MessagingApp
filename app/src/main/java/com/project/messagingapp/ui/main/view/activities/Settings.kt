package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.messagingapp.ui.main.view.fragments.SettingsFragment
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