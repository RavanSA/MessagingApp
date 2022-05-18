package com.project.messagingapp.ui.main.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseApp.initializeApp
import com.google.firebase.database.FirebaseDatabase
import com.project.messagingapp.R
import com.project.messagingapp.ui.main.viewmodel.SplashViewModel
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessaging.getInstance
import com.project.messagingapp.data.model.ChatListRoom
import com.project.messagingapp.databinding.FragmentMainChatListBinding
import com.project.messagingapp.ui.main.viewmodel.ChatListViewModel
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SplashScreen : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var chatListViewModel: ChatListViewModel
    private var testChatList:MutableList<ChatListRoom> = mutableListOf()


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
            FirebaseApp.initializeApp(this@SplashScreen)
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        val token = it.result
                        val databaseReference =
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(AppUtil().getUID()!!)

                        val map: MutableMap<String, Any> = HashMap()
                        map["token"] = token!!
                        databaseReference.updateChildren(map)
                    }
                })

            chatListViewModel = ViewModelProvider(this)[ChatListViewModel::class.java]

            lifecycle.coroutineScope.launch {
                chatListViewModel.getChatListWithFlow().collect() {
                    testChatList = it
                    Log.d("TESTFLOW", it.toString())
                }
            }

//            registratedUser.putExtra("flow_chat_list",testChatList)

            startActivity(registratedUser)
        } else {
            startActivity(notRegistratedUser)
        }

    }

}