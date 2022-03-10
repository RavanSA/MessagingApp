package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.project.messagingapp.databinding.ActivityMessageBinding
import com.project.messagingapp.ui.main.viewmodel.MessageViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessageActivity : AppCompatActivity() {
    private lateinit var messageBinding: ActivityMessageBinding
    private var receiverID: String? = null
    private lateinit var msgViewModel: MessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msgViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application))[MessageViewModel::class.java]

        messageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(messageBinding.root)

        receiverID = intent.getStringExtra("id_receiver")

        messageBinding.btnSend.setOnClickListener {
            val msgText = messageBinding.msgText.text.toString()
            if(msgText.isNotEmpty()){
                GlobalScope.launch {
                    msgViewModel.sendMessage(msgText, receiverID!!)
                }
            }
        }
    }
}