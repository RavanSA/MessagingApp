package com.project.messagingapp.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.project.messagingapp.R
import com.project.messagingapp.utils.AppUtil
import com.project.messagingapp.utils.VideoCallJSInterface
import kotlinx.android.synthetic.main.activity_video_call.*
import java.util.*

class VideoCallActivity : AppCompatActivity() {

    private var receiverID: String? = null
    private var firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
    var friendsUsername = ""

    var isPeerConnected = false

    var isAudio = true
    var isVideo = true

    var uniqueId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        receiverID = intent.getStringExtra("receiverID")

        callBtn.setOnClickListener {
            friendsUsername = friendNameEdit.text.toString()
            sendCallRequest()
        }

        toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_baseline_mic_24 else R.drawable.ic_baseline_mic_off_24 )
        }

        toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24 )
        }

        setUpWebView()
    }

    private fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show()
            return
        }

        receiverID?.let { firebaseRef.child(it).child("calls").setValue(AppUtil().getUID()!!) }
        listenForConnId()
    }

    private fun listenForConnId() {
//        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {}
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.value == null)
//                    return
//                switchToControls()
//                callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
//            }
        switchToControls()
        callJavascriptFunction("javascript:startCall(\"${receiverID}\")")
    }


    //1
    private fun setUpWebView() {
        webView.webChromeClient = object: WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(VideoCallJSInterface(this), "Android")

        loadVideoCall()
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        webView.loadUrl(filePath)

        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    private fun initializePeer() {

        uniqueId = getUniqueID()

        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        //TODO FIREBASE SETTINGS FOR WEBRTC
//        firebaseRef.child(username).child("incoming").addValueEventListener(object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {}
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                onCallRequest(snapshot.value as? String)
//            }
//        })
        onCallRequest(AppUtil().getUID()!!)
    }

    private fun onCallRequest(caller: String?) {
        if (caller == null) return

        callLayout.visibility = View.VISIBLE
        incomingCallTxt.text = "$caller is calling..."

        acceptBtn.setOnClickListener {
//            firebaseRef.child(username).child("connId").setValue(uniqueId)
//            firebaseRef.child(username).child("isAvailable").setValue(true)

            callLayout.visibility = View.GONE
            switchToControls()
        }

        rejectBtn.setOnClickListener {
            firebaseRef.child(friendsUsername).child("calls").setValue("")
            callLayout.visibility = View.GONE
        }
    }

    private fun switchToControls() {
            inputLayout.visibility = View.GONE
            callControlLayout.visibility = View.VISIBLE
    }

    fun onPeerConnected() {
        isPeerConnected = true
    }


    private fun getUniqueID(): String {
        return UUID.randomUUID().toString()
    }

    private fun callJavascriptFunction(functionString: String) {
        webView.post { webView.evaluateJavascript(functionString, null) }
    }


    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        receiverID?.let { firebaseRef.child(it).child("calls").setValue("") }
        webView.loadUrl("about:blank")
        super.onDestroy()
    }

}