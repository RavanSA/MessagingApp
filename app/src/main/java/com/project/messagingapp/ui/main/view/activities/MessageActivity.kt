package com.project.messagingapp.ui.main.view.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.databinding.ActivityMessageBinding
import com.project.messagingapp.ui.main.adapter.CustomContactAdapter
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter
import com.project.messagingapp.ui.main.viewmodel.MessageViewModel
import com.project.messagingapp.ui.main.viewmodel.ProfileViewModel
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageActivity : AppCompatActivity() {
    private lateinit var messageBinding: ActivityMessageBinding
    private var receiverID: String? = null
    private lateinit var msgViewModel: MessageViewModel
    private lateinit var messageViewModel: MessageViewModel
    private var messageAdapter: MessageRecyclerAdapter? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msgViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application))[MessageViewModel::class.java]

        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]


        messageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(messageBinding.root)

        receiverID = intent.getStringExtra("id_receiver")

        messageViewModel.checkChat(receiverID!!).observe(this, Observer { bool ->
            Log.d("BOOLCHECKCHAT",bool.toString())
        })

        messageBinding.btnSend.setOnClickListener {
            val msgText = messageBinding.msgText.text.toString()
            if(msgText.isNotEmpty()){
                GlobalScope.launch {
                    msgViewModel.sendMessage(msgText, receiverID!!)
                }
            }
        }

        getChatID(receiverID!!)
    }

    fun getChatID(receiverID:String) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                msgViewModel.getChatID(receiverID).observe(this@MessageActivity,{ data ->
                    readMessage(data.chatList!!)
                    Log.d("CHATLIST", data.chatList!!.size.toString())
                })
            }
        }
    }

    fun readMessage(allMessages: List<ChatListModel>){
            msgViewModel.readMessages(allMessages).observe(this@MessageActivity,{ data ->
                callAdapter(data)
            })
    }

    fun callAdapter (data: MutableList<MessageModel>){
        messageBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        messageAdapter = MessageRecyclerAdapter(data)
        messageBinding.messageRecyclerView.adapter = messageAdapter
        messageAdapter!!.notifyDataSetChanged()
    }

//    override fun onPause() {
//        super.onPause()
//        AppUtil().updateOnlineStatus("offline")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        AppUtil().updateOnlineStatus("online")
//    }
}