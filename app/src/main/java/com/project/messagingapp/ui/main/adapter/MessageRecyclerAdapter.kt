package com.project.messagingapp.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.data.model.MessageModel
import com.project.messagingapp.databinding.LeftMessageItemBinding
import com.project.messagingapp.databinding.RightMessageLayoutBinding
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter.*
import com.project.messagingapp.utils.AppUtil

class MessageRecyclerAdapter(
    private val messages: MutableList<MessageModel>?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {
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
        val messageModel: MessageModel = getItem(position) as MessageModel
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
            fun bind(messages: MessageModel){
                rightDataBinding.txtMessage.text = messages.message
            }
        }

    inner class MessageViewHolderLeft(var leftDataBinding: LeftMessageItemBinding):
        RecyclerView.ViewHolder(leftDataBinding.root){
            fun bind(messages: MessageModel){
                leftDataBinding.txtMessage.text = messages.message
            }
        }

    override fun getItemCount(): Int {
        return messages!!.size
    }

}