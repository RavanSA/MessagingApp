package com.project.messagingapp.utils

import android.webkit.JavascriptInterface
import com.project.messagingapp.ui.main.view.activities.VideoCallActivity

class VideoCallJSInterface(val videoCallActivity: VideoCallActivity) {

    @JavascriptInterface
    public fun onPeerConnected(){
        videoCallActivity.onPeerConnected()
    }
}