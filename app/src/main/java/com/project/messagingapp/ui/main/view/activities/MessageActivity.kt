package com.project.messagingapp.ui.main.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.pm.PackageManager
import android.media.MediaRecorder
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
import androidx.core.view.isGone
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
import io.ak1.pix.helpers.hide
import io.ak1.pix.helpers.show
import kotlinx.android.synthetic.main.activity_main_chat_screen.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.pick_image_dialog.view.*
import kotlinx.android.synthetic.main.pick_image_dialog.view.imageFromCamera
import kotlinx.android.synthetic.main.pick_image_dialog.view.imageFromStorage
import kotlinx.android.synthetic.main.send_document_dialog.view.*
import kotlinx.android.synthetic.main.settings_fragment.*
import kotlinx.android.synthetic.main.toolbar_message.*
import org.json.JSONObject
import kotlinx.coroutines.flow.collect
import java.io.File
import java.io.IOException
import java.util.*

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
    private var recorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private var recordStart = 0L
    private var lastAudioFile=""
    private var recordDuration = 0L
    private val REQ_AUDIO_PERMISSION=29

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiverID = intent.getStringExtra("id_receiver")


        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]


        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                getUserMessages()
            }
        }

        Log.d("CHECKCHATBOOL1",checkChatBool.toString())

        messageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(messageBinding.root)

        messageBinding.btnSend.setOnClickListener {
            val msgTextString = messageBinding.msgText.text.toString()
            if(msgTextString.isNotEmpty()){
                Log.d("MESSAGEISEMPTY","TRUE")
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

        messageBinding.msgText.addTextChangedListener(msgTxtChangeListener)

        msgBack.setOnClickListener {
                val contactActivity = Intent(
                    this@MessageActivity,
                    UserContacts::class.java
                )
                startActivity(contactActivity)
        }

        messageBinding.imgRecord.setOnClickListener {
            if(AppUtil().checkPermission(this, Manifest.permission.RECORD_AUDIO,reqCode = REQ_AUDIO_PERMISSION))
                startRecording()
        }

        lifecycle.coroutineScope.launch {
            messageViewModel.getUserMessageFromRoomDb(receiverID!!).collect() {
                callAdapter(it)
            }
        }

        messageBinding.lottieVoice.setOnClickListener {
            if (isRecording){
                stopRecording()
                val duration=(recordDuration/1000).toInt()
                if (duration<=1) {
                    Toast.makeText(this,"Nothing is created",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                receiverID?.let { it1 -> messageViewModel.sendVoiceMessage(lastAudioFile, it1) }
            }
        }

    }

    private fun startRecording() {
        messageBinding.lottieVoice.show()
        messageBinding.msgText.apply {
            hint = "Recording..."
        }

        lastAudioFile=
            "${this.externalCacheDir?.absolutePath}/audiorecord${System.currentTimeMillis()}.mp3"

        Log.d("LASTADIOFILEPATH",lastAudioFile.toString())

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setOutputFile(lastAudioFile)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            try {
                prepare()
            } catch (e: IOException) {
                println("ChatFragment.startRecording${e.message}")
            }
            start()
            isRecording=true
            recordStart = Date().time
        }

    }

    private fun stopRecording() {
        messageBinding.msgText.apply {
            isEnabled=true
            hint="Message"
        }

        recorder?.apply {
            stop()
            release()
            recorder = null
        }
        isRecording=false
        recordDuration = Date().time - recordStart
        messageBinding.lottieVoice.apply {
            isEnabled = false
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
//            checkOnlineStatus(receiverID!!)
        } else if(!internetConnection){
            receiverID?.let { messageViewModel.getUserMessageFromRoomDb(it) }
        }
    }

    private fun getChatID(receiverID:String) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                messageViewModel.getChatID(receiverID).observe(this@MessageActivity,{ data ->
                    data.chatList?.let { readMessage(it) }
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
        val layout:View = LayoutInflater.from(this).inflate(R.layout.send_document_dialog,
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

        layout.selectDocument.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.flags = FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
            getFilesLauncher.launch(intent)
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


    private val msgTxtChangeListener=object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s.isNullOrBlank() || s.isEmpty()) {
                typingStatus("false")
                messageBinding.imgRecord.show()
                messageBinding.btnSend.hide()
            } else {
                typingStatus(receiverID!!)
                messageBinding.btnSend.show()
                messageBinding.imgRecord.hide()
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private var getFilesLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent = it.data!!
            val uri: Uri? = data.data
            receiverID?.let { it1 -> uri?.let { it2 -> messageViewModel.sendDocumentFile(it2, it1) } }
        }
    }

}