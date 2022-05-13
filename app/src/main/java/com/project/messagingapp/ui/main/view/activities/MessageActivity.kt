package com.project.messagingapp.ui.main.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.databinding.ActivityMessageBinding
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter
import com.project.messagingapp.ui.main.viewmodel.MessageViewModel
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.project.messagingapp.R
import com.project.messagingapp.ui.main.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.activity_main_chat_screen.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.settings_fragment.*
import kotlinx.android.synthetic.main.toolbar_message.*
import org.json.JSONObject


class MessageActivity : AppCompatActivity() {
    private lateinit var messageBinding: ActivityMessageBinding
    private var receiverID: String? = null
    private lateinit var messageViewModel: MessageViewModel
    private var messageAdapter: MessageRecyclerAdapter? = null
    private var checkChatBool: String? = null
    private var userName: String? = null


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
            val msgTextString = messageBinding.msgText.text.toString()
            if(msgTextString.isNotEmpty()){
                    sendMessageObserve(msgTextString,receiverID!!)
                    msgText.setText("")
                    getChatID(receiverID!!)
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


        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                getChatID(receiverID!!)
            }
        }


        msgBack.setOnClickListener {
                val contactActivity = Intent(
                    this@MessageActivity,
                    UserContacts::class.java
                )
                startActivity(contactActivity)
        }
        checkOnlineStatus(receiverID!!)
    }

    private fun getChatID(receiverID:String) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                messageViewModel.getChatID(receiverID).observe(this@MessageActivity,{ data ->
                    readMessage(data.chatList!!)
                })
            }
        }
    }

    private suspend fun checkChatCreated(receiverID: String): String? {
            messageViewModel.checkChatCreated(receiverID)
                .observe(this@MessageActivity, Observer { data ->
                    checkChatBool = data
                })
        return checkChatBool
    }

    private fun readMessage(allMessages: List<ChatListModel>){
            messageViewModel.readMessages(allMessages).observe(this@MessageActivity,{ data ->
                callAdapter(data)
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun callAdapter (data: MutableList<MessageModel>) {
        messageBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        messageAdapter = MessageRecyclerAdapter(data)
        messageBinding.messageRecyclerView.adapter = messageAdapter
        messageBinding.messageRecyclerView.postDelayed({
            messageBinding.messageRecyclerView.scrollToPosition(
                (messageBinding.messageRecyclerView.adapter?.itemCount?.minus(1)!!))
        }, 100)
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
                    sendMessage(message, receiverID)
                    getToken(message,receiverID,userName!!)
                } else {
                    createChat(message, receiverID)
//                    messageViewModel.createChatIfNotExist()
                }

        }
    }

    private fun checkOnlineStatus( receiverID: String) {
       lifecycleScope.launch {
           messageViewModel.checkOnlineStatus(receiverID).observe(this@MessageActivity, Observer {
               messageBinding.online = it.online

               val typing = it.typing
               userName = it.name

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

    private fun getToken(
        message: String,
        receiverID: String,
        name: String
    ) {
        messageViewModel.getToken(message,receiverID,name).observe(this@MessageActivity, Observer { data ->
            Log.d("TOKEN1", data.toString())
            sendNotification(data)
        })
    }

    private fun sendNotification(to: JSONObject) {
        val sendNotifi = messageViewModel.sendNotification(to)
        val requestQueue = Volley.newRequestQueue(this)
        sendNotifi.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
            Log.d("ACTIVISENDNOTIFICATION", sendNotifi.toString())
        requestQueue.add(sendNotifi)

    }

}