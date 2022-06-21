package com.project.messagingapp.ui.main.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.SharedPreferences
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
import androidx.annotation.RequiresApi
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
import com.project.messagingapp.databinding.ToolbarMessageBinding
import com.project.messagingapp.ui.main.viewmodel.ChatListViewModel
import com.project.messagingapp.ui.main.viewmodel.RegistrationViewModel
import com.project.messagingapp.utils.AES
import com.project.messagingapp.utils.Classifier
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
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class MessageActivity : AppCompatActivity() {


    private lateinit var messageBinding: ActivityMessageBinding
    private var receiverID: String? = null
    private var userName: String? = null
    private var receiverImage: String? = null
    private lateinit var messageViewModel: MessageViewModel

    private var messageAdapter: MessageRecyclerAdapter? = null
    private var checkChatBool: String? = null
    private var receiver_userName: String? = null
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
    private val MODEL_ASSETS_PATH = "model_conv_lstm.tflite"

    private val INPUT_MAXLEN = 300

    private var tfLiteInterpreter : Interpreter? = null
    private val classifier = Classifier(this , "word_dict.json" , INPUT_MAXLEN)



    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiverID = intent.getStringExtra("id_receiver")
        receiver_userName = intent.getStringExtra("chat_list_receiver_name")
        receiverImage = intent.getStringExtra("chat_list_receiver_image")

        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        tfLiteInterpreter = Interpreter( loadModelFile() )


        val progressDialog = ProgressDialog( this )
        progressDialog.setMessage( "Parsing word_dict.json ..." )
        progressDialog.setCancelable( false )
        progressDialog.show()
        classifier.processVocab( object: Classifier.VocabCallback {
            override fun onVocabProcessed() {
                progressDialog.dismiss()
            }
        })


        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                getUserMessages()
            }
        }

        messageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(messageBinding.root)

        Glide.with(this).load(receiverImage)
            .into(msgImage)

        messageBinding.btnSend.setOnClickListener {
            val msgTextString = messageBinding.msgText.text.toString()
            if(msgTextString.isNotEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    Log.d("MESSAGEISEMPTY", "TRUE")
                    withContext(Dispatchers.Main) {
                        sendMessageObserve(msgTextString, receiverID!!)
                        msgText.setText("")
                    }
                }
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

        messageBinding.imageFile.setOnClickListener {
            updateImageDialog()
        }

        messageBinding.msgText.addTextChangedListener(msgTxtChangeListener)

        msgBack.setOnClickListener {
                val contactActivity = Intent(
                    this@MessageActivity,
                    MainChatScreen::class.java
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

    private suspend fun getUserMessages(){
        val internetConnection = AppUtil().checkInternetConnection(this)
        if(internetConnection){
            checkChatBool = checkChatCreated(receiverID!!)
            getChatID(receiverID!!)
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
                for(element in data){
                    val chatRoom = ChatRoom(
                        element.messageKey,element.chatID,element.date,element.message,
                        element.receiverId,element.senderId,element.type
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        messageViewModel.addNewMessage(
                            chatRoom
                        )
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
            })
    }

    private suspend fun sendMessage(message: String, receiverID: String){
        messageViewModel.sendMessage(message, receiverID)
            .observe(this@MessageActivity, Observer {
            })
    }

    private suspend fun sendMessageObserve(message: String, receiverID: String) {
            Log.d("ACTIVITYCHECKCHAT", checkChatBool.toString())
                val bool = !checkChatCreated(receiverID).isNullOrEmpty()

                if (bool) {
                        sendMessage(AES.encrypt(message), receiverID)
                    userName?.let { getToken(message, receiverID, it) }
                    recognizeEmotion(message)
                } else {
                    createChat(AES.encrypt(message), receiverID)
                    recognizeEmotion(message)
                }
        }


    private fun checkOnlineStatus( receiverID: String) {
       lifecycleScope.launch {
           messageViewModel.checkOnlineStatus(receiverID).observe(this@MessageActivity, Observer {

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
            dialog.dismiss()

        }

        layout.selectLocation.setOnClickListener{
            sendCurrentLocation()
            dialog.dismiss()

        }

        dialog = updateDialog.create()
        dialog.show()
    }

    private fun sendCurrentLocation() {
        val sharedPref : SharedPreferences = this@MessageActivity.
        getSharedPreferences("com.project.messaginapp.location", Context.MODE_PRIVATE)

        val currentLocation = sharedPref.getString("LOCATION",null )
        receiverID?.let { currentLocation?.let { it1 -> messageViewModel.sendCurrentLocation(it1, it) } }
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


    private fun recognizeEmotion(message: String){
        val appUtil = AppUtil()
        val tokenizedMessage = classifier.tokenize(message.lowercase(Locale.getDefault()).trim().toString())
        val paddedMessage = classifier.padSequence(tokenizedMessage)

        val results = classifySequence(paddedMessage)
        val maxResultValue = results.maxOrNull()

        val label1 = results[0]
        val label2 = results[1]
        val label3 = results[2]
        val label4 = results[3]
        val label5 = results[4]
        val label6 = results[5]

        when (maxResultValue) {
            results[0] -> {
                //sadness
                appUtil.setUserMood("#2a3b90")
            }
            results[1] -> {
                //sadness
                appUtil.setUserMood("#000000")
            }
            results[2] -> {
                //anger
                appUtil.setUserMood("#FF0000")
            }
            results[3] -> {
                //surprise
                appUtil.setUserMood("#e3e3e3")
            }
            results[4] -> {
                //fear
                appUtil.setUserMood("#2f2323")
            }
            results[5] -> {
                //joy
                appUtil.setUserMood("#821747")
            }
        }

    }

    private fun loadModelFile(): MappedByteBuffer {

        val assetFileDescriptor = assets.openFd(MODEL_ASSETS_PATH)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun classifySequence (sequence : FloatArray ): FloatArray {

//        val twoDimStringArray= arrayOf(
//            floatArrayOf(
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,
//                0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F,    0F, 1293F,
//                3849F, 4779F, 4322F)
//        )

        val inputs : Array<FloatArray> = arrayOf(
            sequence
        )

        val outputs : Array<FloatArray> = arrayOf(FloatArray( 6 ))

        tfLiteInterpreter?.run( inputs , outputs )

        return outputs[0]
    }

}