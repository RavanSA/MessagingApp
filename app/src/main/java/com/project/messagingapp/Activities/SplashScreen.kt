package com.project.messagingapp.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.project.messagingapp.R


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed(Runnable {
            val NotRegistratedUser = Intent(
                this@SplashScreen,
               RegistrationActivity::class.java
            )
            val RegistratedUser = Intent(
                this@SplashScreen,
                MainChatScreen::class.java
            )

            val sharedPref : SharedPreferences = this@SplashScreen.getSharedPreferences("com.project.messaginapp.phonenumber",Context.MODE_PRIVATE);
            val number = sharedPref.getString("PhoneNumber",null )

            if (number !== null){
                startActivity(RegistratedUser)
            } else {
                startActivity(NotRegistratedUser)
            }

            finish()

        }, 2000)
    }
}
