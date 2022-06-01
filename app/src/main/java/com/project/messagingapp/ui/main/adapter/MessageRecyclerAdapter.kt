package com.project.messagingapp.ui.main.adapter

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.messagingapp.data.model.ChatRoom
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.databinding.*
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter.*
import com.project.messagingapp.utils.AppUtil
import io.ak1.pix.helpers.show
import kotlinx.android.synthetic.main.nearby_user_item.view.*
import kotlinx.android.synthetic.main.right_audio_message_item.view.*
import java.io.IOException
import com.project.messagingapp.R
import android.content.Intent

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.project.messagingapp.ui.main.view.activities.MessageActivity
import com.project.messagingapp.ui.main.view.activities.WebView


class MessageRecyclerAdapter(
    private val messages: MutableList<ChatRoom>?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var player = MediaPlayer()
    private var lastPlayedHolder: LeftAudioMessageItemBinding?=null
    private var lastReceivedPlayedHolder: RightAudioMessageItemBinding?=null

    fun stopPlaying() {
        if(player.isPlaying) {
//            lastReceivedPlayedHolder?.progressBar?.abandon()
//            lastPlayedHolder?.progressBar?.abandon()
            lastReceivedPlayedHolder?.imgPlay?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            lastPlayedHolder?.imgPlay?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            player.apply {
                stop()
                reset()
            }
        }
    }

    fun isPlaying() = player.isPlaying


override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        lateinit var dataBindingRight: RightMessageLayoutBinding
        lateinit var dataBindingLeft: LeftMessageItemBinding
        lateinit var dataBindingRightAudio: RightAudioMessageItemBinding
        lateinit var dataBindingLeftAudio: LeftAudioMessageItemBinding
        lateinit var dataBindingRightFile: RightFileMessageItemBinding
        lateinit var dataBindingLeftFile: LeftFileMessageItemBinding
        if (viewType == 0) {
            dataBindingRight = RightMessageLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }

        if (viewType == 1) {
            dataBindingLeft = LeftMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }

        if (viewType == 10) {
            dataBindingRightAudio = RightAudioMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }

        if (viewType == 11) {
            dataBindingLeftAudio = LeftAudioMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }

        if (viewType == 100) {
            dataBindingRightFile = RightFileMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }

        if (viewType == 110) {
            dataBindingLeftFile = LeftFileMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }

         if (viewType == 0) {
           return MessageViewHolderRight(dataBindingRight)
        } else if (viewType == 1) {
           return MessageViewHolderLeft(dataBindingLeft)
        } else if (viewType == 10) {
            return RightAudioMessageViewHolder(dataBindingRightAudio)
        } else if (viewType == 11) {
            return LeftAudioMessageViewHolder(dataBindingLeftAudio)
        } else if (viewType == 100) {
             return RightFileMessageViewHolder(dataBindingRightFile)
         } else if (viewType == 110) {
             return LeftFileMessageViewHolder(dataBindingLeftFile)
         }


        return super.createViewHolder(parent,viewType)
    }

    override fun getItemViewType(position: Int): Int {
        val messageModel: ChatRoom = getItem(position) as ChatRoom
        if (messageModel.senderId == AppUtil().getUID()!! &&
            (messageModel.type == "image" || messageModel.type == "text")){
            return 0
        } else if(messageModel.senderId == AppUtil().getUID()!! && messageModel.type == "audio") {
            return 10
        } else if(messageModel.senderId !== AppUtil().getUID()!! && messageModel.type == "audio") {
            return 11
        }else if(messageModel.senderId == AppUtil().getUID()!! && messageModel.type == "document") {
            return 100
        } else if(messageModel.senderId !== AppUtil().getUID()!! && messageModel.type == "document") {
            return 110
        } else if (messageModel.senderId !== AppUtil().getUID()!! &&
            (messageModel.type == "image" || messageModel.type == "text")){
            return 1
        }

        return super.getItemViewType(position)
    }

    fun getItem(position: Int): Any {
        return messages!![position]
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        if (getItemViewType(position) == 0) {
            (holder as MessageViewHolderRight).bind(messages!![position])
        }

        if (getItemViewType(position) == 1) {
            (holder as MessageViewHolderLeft).bind(messages!![position])
        }

        if(getItemViewType(position) == 10){
            (holder as RightAudioMessageViewHolder).bind(context, messages!![position])
        }

        if(getItemViewType(position) == 11){
            (holder as LeftAudioMessageViewHolder).bind(context, messages!![position])
        }

        if(getItemViewType(position) == 100){
            (holder as RightFileMessageViewHolder).bind(messages!![position])
        }

        if(getItemViewType(position) == 110){
            (holder as LeftAudioMessageViewHolder).bind(context, messages!![position])
        }

    }

    inner class MessageViewHolderRight(var rightDataBinding: RightMessageLayoutBinding) :
        RecyclerView.ViewHolder(rightDataBinding.root){
            fun bind(messages: ChatRoom){
                if(messages.type == "text") {
                    rightDataBinding.txtMessage.text = messages.message
                } else if (messages.type == "image"){
                    rightDataBinding.imageMessage.visibility = View.VISIBLE
                    Glide.with(context).load(messages.message)
                        .into(rightDataBinding.imageMessage)
                }
            }
        }

    inner class MessageViewHolderLeft(var leftDataBinding: LeftMessageItemBinding):
        RecyclerView.ViewHolder(leftDataBinding.root){
            fun bind(messages: ChatRoom){
                if(messages.type == "text") {
                    leftDataBinding.txtMessage.text = messages.message
                } else if (messages.type == "image"){
                    Log.d("IMAGELINK",messages.message)
                    leftDataBinding.leftImageMessage.visibility = View.VISIBLE
                    Glide.with(context).load(messages.message)
                        .into(leftDataBinding.leftImageMessage)
                }
            }
        }

    inner class RightAudioMessageViewHolder(var rightAudioMessageBinding: RightAudioMessageItemBinding )
        : RecyclerView.ViewHolder(rightAudioMessageBinding.root){
            fun bind(context: Context, messages: ChatRoom){
                rightAudioMessageBinding.messageModel = messages
                rightAudioMessageBinding.imgPlay.setOnClickListener {
                    startPlaying(
                        context,
                        messages,
                        rightAudioMessageBinding)
                }
                rightAudioMessageBinding.executePendingBindings()
            }

        private fun startPlaying(
            context: Context,
            messages: ChatRoom,
            binding: RightAudioMessageItemBinding) {
            if (player.isPlaying){
                stopPlaying()
//                lastReceivedPlayedHolder?.progressBar?.abandon()
                lastReceivedPlayedHolder?.imgPlay?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                lastPlayedHolder?.imgPlay?.setImageResource(R.drawable.ic_baseline_play_arrow_24)
//                lastPlayedHolder?.progressBar?.abandon()
//                if (lastPlayedAudioId==item.createdAt)
//                    return

            }
            player= MediaPlayer()
            lastReceivedPlayedHolder = binding

//            lastPlayedAudioId=item.createdAt
//            binding.progressBuffer.show()
            binding.imgPlay.visibility = View.GONE
            player.apply {
                try {
                    setDataSource(context, Uri.parse(messages.message.toUri().toString()))
                    prepareAsync()
                    setOnPreparedListener {
//                        Timber.v("Started..")
                        start()
                        binding.imgPlay.setImageResource(R.drawable.ic_baseline_stop_24)
                        binding.imgPlay.visibility = View.VISIBLE
                        binding.soundWave.visibility = View.VISIBLE
                        binding.horizantalWave.visibility = View.GONE
//                        binding.progressBar.startStories()
                    }
                    setOnCompletionListener {
//                        currentHolder.progressBar.abandon()
                        binding.soundWave.visibility = View.GONE
                        binding.horizantalWave.visibility = View.VISIBLE
                        binding.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                } catch (e: IOException) {
                    println("ERROR OCCURED")
                }
            }
        }
        }




    inner class LeftAudioMessageViewHolder(var leftAudioMessageBinding: LeftAudioMessageItemBinding)
        : RecyclerView.ViewHolder(leftAudioMessageBinding.root){
        fun bind(context: Context, messages: ChatRoom){
            leftAudioMessageBinding.messageModel = messages
            leftAudioMessageBinding.imgPlay.setOnClickListener {
                startPlaying(
                    context,
                    messages,
                    leftAudioMessageBinding)
            }
            leftAudioMessageBinding.executePendingBindings()
        }

        private fun startPlaying(
            context: Context,
            messages: ChatRoom,
            leftAudioMessageBinding: LeftAudioMessageItemBinding
        ) {
                if (player.isPlaying){
                    stopPlaying()
//                    if (lastPlayedAudioId==item.createdAt)
//                        return
                }
                player= MediaPlayer()
                lastPlayedHolder =leftAudioMessageBinding
//                lastPlayedAudioId=item.createdAt
                leftAudioMessageBinding.progressBuffer.show()
                player.apply {
                    try {
                        setDataSource(context, Uri.parse(messages.message.toUri().toString()))
                        prepareAsync()
                        setOnPreparedListener {
                            start()
                            leftAudioMessageBinding.leftSoundWave.visibility = View.VISIBLE
                            leftAudioMessageBinding.leftHorizantalWave.visibility = View.GONE
//                            leftAudioMessageBinding.progressBuffer.gone()
                            leftAudioMessageBinding.imgPlay.setImageResource(R.drawable.ic_baseline_stop_24)
                            leftAudioMessageBinding.imgPlay.show()
//                            leftAudioMessageBinding.progressBar.startStories()
                        }
                        setOnCompletionListener {
                            leftAudioMessageBinding.leftSoundWave.visibility = View.GONE
                            leftAudioMessageBinding.leftHorizantalWave.visibility = View.VISIBLE
                            leftAudioMessageBinding.imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        }
                    } catch (e: IOException) {
                        println("ERROR OCCURED")
                    }
                }
            }
        }

    inner class RightFileMessageViewHolder(var rightFileMessageItemBinding: RightFileMessageItemBinding)
        : RecyclerView.ViewHolder(rightFileMessageItemBinding.root) {
            fun bind(messages: ChatRoom){
                rightFileMessageItemBinding.fileOpener.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(messages.message + ".pdf")
                    it.context.startActivity(intent)
                }
            }
        }


    inner class LeftFileMessageViewHolder(var leftFileMessageItemBinding: LeftFileMessageItemBinding)
        : RecyclerView.ViewHolder(leftFileMessageItemBinding.root){
            fun bind( messages: ChatRoom){
                leftFileMessageItemBinding.fileOpener.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(messages.message + ".pdf")
                    it.context.startActivity(intent)
                }
            }
        }


    override fun getItemCount(): Int {
        return messages!!.size
    }

}