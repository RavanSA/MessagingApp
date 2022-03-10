package com.project.messagingapp.ui.main.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.R
import com.project.messagingapp.ui.main.viewmodel.SplashViewModel


class SplashScreen : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        setContentView(R.layout.activity_splash_screen)

        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        splashViewModel.prepareTime()

        splashViewModel.openAct.observe(this,
            {
                redirectUserTo()
            })
    }

    private fun redirectUserTo(){
        val notRegistratedUser = Intent(
            this@SplashScreen,
            RegistrationActivity::class.java
        )

        val registratedUser = Intent(
            this@SplashScreen,
            MainChatScreen::class.java
        )

        val sharedPref : SharedPreferences = this@SplashScreen.
        getSharedPreferences("com.project.messaginapp.phonenumber",Context.MODE_PRIVATE)

        val number = sharedPref.getString("PhoneNumber",null )

        if (number !== null){
            startActivity(registratedUser)
        } else {
            startActivity(notRegistratedUser)
        }

    }

}