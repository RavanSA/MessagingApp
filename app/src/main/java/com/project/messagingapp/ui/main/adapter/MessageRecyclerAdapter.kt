package com.project.messagingapp.ui.main.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.messagingapp.data.model.ChatRoom
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.databinding.LeftMessageItemBinding
import com.project.messagingapp.databinding.RightMessageLayoutBinding
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter.*
import com.project.messagingapp.utils.AppUtil
import kotlinx.android.synthetic.main.nearby_user_item.view.*

class MessageRecyclerAdapter(
    private val messages: MutableList<ChatRoom>?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {
        context = parent.context
        lateinit var dataBindingRight: RightMessageLayoutBinding
        lateinit var dataBindingLeft: LeftMessageItemBinding
        if( viewType == 0){
                dataBindingRight = RightMessageLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            }

        if(viewType == 1){
            dataBindingLeft = LeftMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }

        return if (viewType == 0) MessageViewHolderRight(dataBindingRight)
        else MessageViewHolderLeft(dataBindingLeft)
    }

    override fun getItemViewType(position: Int): Int {
        val messageModel: ChatRoom = getItem(position) as ChatRoom
        return if (messageModel.senderId == AppUtil().getUID()!!) 0 else 1
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
    }

    inner class MessageViewHolderRight(var rightDataBinding: RightMessageLayoutBinding) :
        RecyclerView.ViewHolder(rightDataBinding.root){
            fun bind(messages: ChatRoom){
                Log.d("DETECTINGMESSAGETYPE","...")
                if(messages.type == "text") {
                    Log.d("TEXTMESSAGEDETECTED","TRUE")
                    rightDataBinding.txtMessage.text = messages.message
                } else if (messages.type == "image"){
                    Log.d("IMAGELINK",messages.message)
                    Log.d("IMAGEMESSAGEDETECTED","TRUE")
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

    override fun getItemCount(): Int {
        return messages!!.size
    }

}