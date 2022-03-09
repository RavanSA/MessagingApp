package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.R
import com.project.messagingapp.data.model.Response
import com.project.messagingapp.data.repository.remote.ChatRepositoryImpl
import com.project.messagingapp.databinding.ActivityMessageBinding
import com.project.messagingapp.ui.main.viewmodel.MessageViewModel
import com.project.messagingapp.ui.main.viewmodel.ProfileViewModel
import com.project.messagingapp.ui.main.viewmodel.UserRegistrationViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MessageActivity : AppCompatActivity() {
    //    private var receiverImage: String? = null
    private lateinit var messageBinding: ActivityMessageBinding
    private var receiverID: String? = null
    private var conversationID: String? = null
    private lateinit var msgViewModel: MessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msgViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application))[MessageViewModel::class.java]

        messageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(messageBinding.root)

        receiverID = intent.getStringExtra("id_receiver")

//        when (val additionResponse = viewModel.isChatChecked.value) {
//            is Response.Loading -> Log.d("TESTT", "LOADING")
//            is Response.Success -> conversationID
//            is Response.Error -> Log.d("ERRORACTIVITY", additionResponse.message.toString())
//        }
//
//        when (val additionResponse = viewModel.isChatAddedState.value) {
//            is Response.Loading -> Log.d("TESTT", "LOADING")
//            is Response.Success -> conversationID
//            is Response.Error -> Log.d("ERRORACTIVITY", additionResponse.message.toString())
//        }

//        msgViewModel.checkChat(receiverID!!).observe(this, androidx.lifecycle.Observer { boolean ->
//            conversationID = boolean.toString()
//            Log.d("MESSAGEBOOLEAN",boolean.toString())
//        })
        messageBinding.btnSend.setOnClickListener {
            GlobalScope.launch {
                msgViewModel.sendMessage("TEST", receiverID!!,"asd")
                Log.d("SENDMESSAGE", "SENDMESSAGE")
            }
        }

        Log.d("TEST", conversationID.toString())
    }
}