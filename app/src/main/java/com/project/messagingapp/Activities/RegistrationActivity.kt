package com.project.messagingapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.messagingapp.Fragments.GetNumber
import com.project.messagingapp.Fragments.WelcomeFragment
import com.project.messagingapp.R

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, WelcomeFragment())
            .commit()
    }
}