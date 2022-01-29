package com.project.messagingapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.project.messagingapp.Fragments.GetNumber
import com.project.messagingapp.Fragments.WelcomeFragment
import com.project.messagingapp.R

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        setContentView(R.layout.activity_registration)

        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, WelcomeFragment())
            .commit()
    }
}