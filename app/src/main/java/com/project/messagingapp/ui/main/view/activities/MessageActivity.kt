package com.project.messagingapp.ui.main.view.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
import kotlinx.coroutines.*
import kotlin.properties.Delegates
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener


class MessageActivity : AppCompatActivity() {
    private lateinit var messageBinding: ActivityMessageBinding
    private var receiverID: String? = null
//    private lateinit var msgViewModel: MessageViewModel
    private lateinit var messageViewModel: MessageViewModel
    private var messageAdapter: MessageRecyclerAdapter? = null
    private var checkChatBool: String? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiverID = intent.getStringExtra("id_receiver")

        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                checkChatBool = checkChatCreated(receiverID!!)
            }
        }

        Log.d("CHECKCHATBOOL1",checkChatBool.toString())

        messageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(messageBinding.root)

        messageBinding.btnSend.setOnClickListener {
            val msgText = messageBinding.msgText.text.toString()
            if(msgText.isNotEmpty()){
                    sendMessageObserve(msgText,receiverID!!)
            }
        }

        messageBinding.msgText.addTextChangedListener { object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())
                    typingStatus("false")
                else
                    typingStatus(receiverID!!)
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("Not yet implemented")
            }

        } 
        }

        getChatID(receiverID!!)
        checkOnlineStatus(receiverID!!  )
    }

    private fun getChatID(receiverID:String) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                messageViewModel.getChatID(receiverID).observe(this@MessageActivity,{ data ->
                    readMessage(data.chatList!!)
                    Log.d("CHATLIST", data.chatList!!.size.toString())
                })
            }
        }
    }

    private suspend fun checkChatCreated(receiverID: String): String? {
            messageViewModel.checkChatCreated(receiverID)
                .observe(this@MessageActivity, Observer { data ->
                    Log.d("CHECKCHATCREATED", data.toString())
                    checkChatBool = data
                })
        return checkChatBool
    }

    private fun readMessage(allMessages: List<ChatListModel>){
//        lifecycleScope.launch {
//            withContext(Dispatchers.Main) {
//                checkChatBool = checkChatCreated(receiverID!!)
//            }
//        }
            messageViewModel.readMessages(allMessages).observe(this@MessageActivity,{ data ->
                callAdapter(data)
            })
    }

    private fun callAdapter (data: MutableList<MessageModel>) {
        messageBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        messageAdapter = MessageRecyclerAdapter(data)
        messageBinding.messageRecyclerView.adapter = messageAdapter
        messageAdapter!!.notifyDataSetChanged()
    }

    private suspend fun createChat(message: String, receiverID: String){
            messageViewModel.createChat(message,receiverID).observe(this@MessageActivity, Observer {
                Log.d("CHAT CREATED","")
            })
    }

    private suspend fun sendMessage(message: String, receiverID: String){
        messageViewModel.sendMessage(message, receiverID)
            .observe(this@MessageActivity, Observer {
                Log.d("MessageSended",it.toString())
            })
    }

    private fun sendMessageObserve(message: String, receiverID: String) {
        lifecycleScope.launch {
            Log.d("ACTIVITYCHECKCHAT", checkChatBool.toString())
                val bool = !checkChatCreated(receiverID).isNullOrEmpty()

                if (bool) {
                    Log.d("IFACTVIITYCHECKCHAT", checkChatBool.toString())
                    sendMessage(message, receiverID)
                } else {
                    createChat(message, receiverID)
                }

        }
    }

    private fun checkOnlineStatus( receiverID: String) {
       lifecycleScope.launch {
           messageViewModel.checkOnlineStatus(receiverID).observe(this@MessageActivity, Observer {
               messageBinding.online = it.online

               val typing = it.typing

               if( typing == AppUtil().getUID()){
                   messageBinding.lottieAnimation.visibility = View.VISIBLE
                   messageBinding.lottieAnimation.playAnimation()
               } else {
                   messageBinding.lottieAnimation.visibility = View.INVISIBLE
                   messageBinding.lottieAnimation.cancelAnimation()
               }
           })
       }
    }

    private fun typingStatus(typing: String){
        lifecycleScope.launch {
            messageViewModel.typingStatus(typing).observe(this@MessageActivity, {

            })
        }
    }

    override fun onResume() {
        super.onResume()
        AppUtil().updateOnlineStatus("online")
    }

    override fun onPause() {
        super.onPause()
        AppUtil().updateOnlineStatus("offline")
    }

//    private fun typing

}