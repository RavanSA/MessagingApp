package com.project.messagingapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.project.messagingapp.Fragments.ProfileFragment
import com.project.messagingapp.Fragments.WelcomeFragment
import com.project.messagingapp.R

class MainChatScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat_screen)

        supportFragmentManager.beginTransaction()
            .add(R.id.chat_container, ProfileFragment())
            .commit()
    }
}