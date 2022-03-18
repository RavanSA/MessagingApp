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

//        var data: MutableLiveData<List<MessageModel>> = msgViewModel.readMessages()
//
//        messageAdapter = MessageRecyclerAdapter(data)
//        contactBinding.recyclerViewContact.adapter = contactAdapter
//        contactAdapter!!.notifyDataSetChanged()
//        lifecycleScope.launch {
//            withContext(Dispatchers.Main){
//                msgViewModel.readMessages(receiverID!!).observe( this@MessageActivity, Observer { data ->
//                    Log.d("REPOVIEWMODEL",data.toString())
//                    messageAdapter = MessageRecyclerAdapter(data)
//                    messageBinding.messageRecyclerView.adapter = messageAdapter
//                    messageAdapter!!.notifyDataSetChanged()
//                })
//            }
//        }
        getChatID(receiverID!!)
//        chatList2?.let { readMessage(it) }
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
        lifecycleScope.launch {
//            msgViewModel.readMessages(allMessages).observe(this@MessageActivity,{ data ->
//                data.messageList?.let { callAdapter(it) }
//                Log.d("MESSAGESLIST", data.messageList.toString())
//            })
            val adapterInput = msgViewModel.readMessages(allMessages)
            callAdapter(adapterInput)
        }
    }

    fun callAdapter (data: List<MessageModel>){
        messageBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        messageAdapter = MessageRecyclerAdapter(data)
        messageBinding.messageRecyclerView.adapter = messageAdapter
        messageAdapter!!.notifyDataSetChanged()
    }
}

