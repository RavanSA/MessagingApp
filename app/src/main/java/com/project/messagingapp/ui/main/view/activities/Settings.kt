package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.graphics.Insets.add
import androidx.fragment.app.viewModels
import com.project.messagingapp.ui.main.view.fragments.SettingsFragment
import com.project.messagingapp.R
import com.project.messagingapp.data.model.SettingList
import com.project.messagingapp.databinding.SettingListBinding
import com.project.messagingapp.ui.main.viewmodel.SettingsViewModel

class Settings : AppCompatActivity() {
    private lateinit var binding: SettingListBinding

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.beginTransaction()
            .add(R.id.setting_container, SettingsFragment())
            .commit()

    }

}