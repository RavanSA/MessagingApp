package com.project.messagingapp.ui.main.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.project.messagingapp.R
import kotlinx.android.synthetic.main.toolbar.*

class MainChatScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat_screen)

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener{
            Toast.makeText(applicationContext,"Navigation Clicked",Toast.LENGTH_LONG).show()
        }
//        toolbar.setOverflowicon(getDrawable(R.id.redIcon))
//        val d: Drawable = "R.drawable.ic_baseline_person_add_24"
//        toolbar.setOv
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemView = item.itemId

        when(itemView){
            R.id.settings ->
                 {
                     startActivity(Intent(
                         this@MainChatScreen,
                         Settings::class.java
                     ))
                     Toast.makeText(applicationContext,"CLICK",Toast.LENGTH_LONG).show()
                }
//                supportFragmentManager.beginTransaction()
//                    .add(R.id.chat_container, ProfileFragment())
//                    .commit()

            R.id.searchuser -> Toast.makeText(applicationContext,"Search User",Toast.LENGTH_LONG).show()
        }

        return false
    }
}