package com.project.messagingapp.ui.main.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
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
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.coroutineScope
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.project.messagingapp.BuildConfig
import com.project.messagingapp.R
import com.project.messagingapp.data.model.ChatRoom
import com.project.messagingapp.ui.main.viewmodel.ChatListViewModel
import com.project.messagingapp.ui.main.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.activity_main_chat_screen.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.pick_image_dialog.view.*
import kotlinx.android.synthetic.main.settings_fragment.*
import kotlinx.android.synthetic.main.toolbar_message.*
import org.json.JSONObject
import kotlinx.coroutines.flow.collect
import java.io.File

class MessageActivity : AppCompatActivity() {


    private lateinit var messageBinding: ActivityMessageBinding
    private var receiverID: String? = null
    private lateinit var messageViewModel: MessageViewModel
    private var messageAdapter: MessageRecyclerAdapter? = null
    private var checkChatBool: String? = null
    private var userName: String? = null
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private val requestcode = 1
    private var imageURI: Uri? = null
    private lateinit var dialog: AlertDialog


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiverID = intent.getStringExtra("id_receiver")


        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]


        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
//                checkChatBool = checkChatCreated(receiverID!!)
//                getChatID(receiverID!!)
                getUserMessages()
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
            }
        }

        msgInfo.setOnClickListener {
            if (!isPermissionGranted()) {
                askPermissions()
            }
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("receiverID", receiverID)
            startActivity(intent)
        }

        imageFile.setOnClickListener {
            updateImageDialog()
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


        msgBack.setOnClickListener {
                val contactActivity = Intent(
                    this@MessageActivity,
                    UserContacts::class.java
                )
                startActivity(contactActivity)
        }

        lifecycle.coroutineScope.launch {
            messageViewModel.getUserMessageFromRoomDb(receiverID!!).collect() {
                callAdapter(it)
            }
        }

    }

//    private fun getMessages(){
//        getChatID(receiverID!!)
//
//    }

    private suspend fun getUserMessages(){
        val internetConnection = AppUtil().checkInternetConnection(this)
        if(internetConnection){
            checkChatBool = checkChatCreated(receiverID!!)
            getChatID(receiverID!!)
            checkOnlineStatus(receiverID!!)
        } else if(!internetConnection){
            receiverID?.let { messageViewModel.getUserMessageFromRoomDb(it) }
        }
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
//                callAdapter(data)
                for(element in data){
                    val chatRoom = ChatRoom(
                        element.messageKey,element.chatID,element.date,element.message,
                        element.receiverId,element.senderId,element.type
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        messageViewModel.addNewMessage(
                            chatRoom
                        )

//                        val testReturnType = messageViewModel.addNewMessage(chatRoom)
//                        messageViewModel.deleteMessageFromFirebase(element.chatID,element.messageKey)
//                        Log.d("testReturnType", testReturnType.toString())
                    }
                }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun callAdapter (data: MutableList<ChatRoom>) {
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
//                    chatListViewModel.contactLastMessageUpdate(
//                        element.message_date,
//                        message,
//                        conversa
//                    )
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



    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestcode)
    }

    private fun isPermissionGranted(): Boolean {

        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
                return false
        }

        return true
    }

    private fun updateImageDialog(){

        val updateDialog = AlertDialog.Builder(this)
        val layout:View = LayoutInflater.from(this).inflate(R.layout.pick_image_dialog,
            null,false)
        updateDialog.setView(layout)

        layout.imageFromCamera.setOnClickListener {
            requestPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )

            dialog.dismiss()
        }

        layout.imageFromStorage.setOnClickListener {
            pickImages.launch("image/*")
            dialog.dismiss()
        }

        dialog = updateDialog.create()
        dialog.show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                GlobalScope.launch (Dispatchers.IO){
                    withContext(Dispatchers.IO) {
                        takePicture()
                    }
                }
            } else {
                Toast.makeText(this,"GİVE PERMISSION TO THE USER",Toast.LENGTH_SHORT).show()
            }
        }

    private suspend fun takePicture() {
        val root =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID + File.separator)
        root.mkdirs()
        val fname = "img_" + System.currentTimeMillis() + ".jpg"
        val sdImageMainDirectory = File(root, fname)

        imageURI = FileProvider.getUriForFile(this, "com.project.messagingapp.provider",
            sdImageMainDirectory)
        Log.d("IMAGEINCAMERA", imageURI.toString())
        val takePictureLauncherVar = takePictureLauncher()
        takePictureLauncherVar.launch(imageURI)
    }

    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
        GlobalScope.launch (Dispatchers.IO){
            withContext(Dispatchers.IO) {
                uri?.let { it ->
                    Log.d("PICKIMAGEMESSAGE", it.toString())

                    receiverID?.let { it1 -> messageViewModel.sendImage(it, it1) }
                }
            }
        }
    }

    private suspend fun takePictureLauncher(): ActivityResultLauncher<Uri> {
        var takePicture: ActivityResultLauncher<Uri>? = null
        takePicture =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
//                Toast.makeText(this, "In Launcher" + imageURI.toString(), Toast.LENGTH_LONG).show()
                if (success) {
                    Log.d("IMAGEURIMESSAGE", imageURI.toString())
                    GlobalScope.launch (Dispatchers.IO){
                    withContext(Dispatchers.IO) {
                        imageURI?.let {
                            receiverID?.let { it1 ->
                                messageViewModel.sendImage(
                                    it,
                                    it1
                                )
                            }
                        }
                    }
                }
            }

        }

        return takePicture!!
    }
}