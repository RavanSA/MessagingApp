package com.project.messagingapp.ui.main.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.project.messagingapp.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebView : AppCompatActivity() {

    private var url: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        app_web_view.settings.setJavaScriptEnabled(true)

        url = intent.getStringExtra("url")

//        Log.d("URL")

        app_web_view.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val uri: Uri = Uri.parse(url)
                return handler(uri)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val uri: Uri = Uri.parse(request?.url.toString())
                return handler(uri)
            }
        }



        app_web_view.loadUrl("https://www.google.co.in/")

    }


    private fun handler(uri: Uri): Boolean {
        val intent = Intent(Intent.ACTION_VIEW,uri)
        startActivity(intent)
        return true
    }
}