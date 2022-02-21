package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import com.project.messagingapp.ui.main.view.fragments.WelcomeFragment
import com.project.messagingapp.R

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        setContentView(R.layout.activity_registration)

        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, WelcomeFragment())
            .commit()

//        val ClassGetUserData = GetUserData()


//        val fm: FragmentManager = supportFragmentManager
//        Fragment frag = fm.findFragmentById(R.id.UserProfile)
//
//
//
//        GetUserData f = (GetUserData) getSupportFragmentManager().findFragmentById(R.id.UserProfile);
//        if(f instanceof ClassGetUserData){
//            UserProfileSave.setOnClickListener {
//                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                ClassGetUserData.CropActivityResultLauncher.launch(takePictureIntent)
//            }
//        }


    }

}