package com.project.messagingapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.messagingapp.R
import com.project.messagingapp.constants.AppConstants
import com.project.messagingapp.ui.main.view.activities.MessageActivity
import java.util.*
import kotlin.collections.HashMap

class FirebaseNotificationService: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        updateToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if (p0.data.isNotEmpty()) {

            val map: Map<String, String> = p0.data

            val name = map["name"]
            val message = map["message"]
            val receiverID = map["receiverID"]
            val conversationID = map["conversationID"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(name!!, message!!, receiverID!!, conversationID!!)
            else createNormalNotification(name!!, message!!, receiverID!!, conversationID!!)
        }

    }

    private fun updateToken(token: String) {

        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(AppUtil().getUID()!!)
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = token
        databaseReference.updateChildren(map)

    }

    private fun createNormalNotification(
        name: String,
        message: String,
        receiveID: String,
        conversationID: String
    ) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
        builder.setContentTitle(name)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.SecondColor, null))
            .setSound(uri)

        val intent = Intent(this, MessageActivity::class.java)

        intent.putExtra("receiveID", receiveID)
        intent.putExtra("convesationID", conversationID)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        name: String,
        message: String,
        receiverID: String,
        conversationID: String
    ) {

        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, MessageActivity::class.java)

        intent.putExtra("receiverID", receiverID)
        intent.putExtra("conversationID", conversationID)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle(name)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.SecondColor, null))
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100, notification)
    }

}