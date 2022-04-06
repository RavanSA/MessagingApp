package com.project.messagingapp.ui.main.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.messagingapp.R
import com.project.messagingapp.data.model.ChatModel
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter
import com.project.messagingapp.ui.main.view.fragments.MainChatList
import com.project.messagingapp.ui.main.view.fragments.WelcomeFragment
import com.project.messagingapp.utils.AppUtil
import kotlinx.android.synthetic.main.activity_main_chat_screen.*
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

        contactFab.setOnClickListener {
            val contactActivity = Intent(
                this@MainChatScreen,
                UserContacts::class.java
            )
            startActivity(contactActivity)
        }

        toolbar.setSubtitleTextColor(getResources().getColor(R.color.black))

        supportFragmentManager.beginTransaction()
            .add(R.id.chatFragmentHolder, MainChatList())
            .commit()
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

            R.id.searchuser -> Toast.makeText(applicationContext,"Search User",Toast.LENGTH_LONG).show()
        }

        return false
    }


//    override fun onResume() {
//        super.onResume()
//        AppUtil().updateOnlineStatus("online")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        AppUtil().updateOnlineStatus("offline")
//    }

}