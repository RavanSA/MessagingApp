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
    private val messages: List<MessageModel>?
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
//                return MessageViewHolderRight(dataBinding)
            }

        if(viewType == 1){
            dataBindingLeft = LeftMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
//            return MessageViewHolderLeft(dataBindingLeft)
        }

        return if (viewType == 0) MessageViewHolderRight(dataBindingRight)
        else MessageViewHolderLeft(dataBindingLeft)
    }


//    override fun onBindViewHolder(
//        holder: MessageViewHolder,
//        position: Int,
//        messageModel: MessageModel
//    ) {
//        super.onBindViewHolder(holder, position, messageModel)
//    }

    override fun getItemViewType(position: Int): Int {
        //CHECK cast expression
        val messageModel: MessageModel = getItem(position) as MessageModel
        Log.d("MESSAGESRECYCLER", messages.toString())
        return if (messageModel.senderId == AppUtil().getUID()!!) 0 else 1
    }

    //CHECK override getItem fun from Adapter
    fun getItem(position: Int): Any {
        return messages!![position]
    }


    //CHECK pass messageModel as parameter
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (getItemViewType(position) == 0) {
            (holder as MessageViewHolderRight).bind(messages!![position])
            Log.d("TEST123","TEST123")
        }

        if (getItemViewType(position) == 1) {
                Log.d("test1","TEST1")
            (holder as MessageViewHolderLeft).bind(messages!![position])
        }
    }

    inner class MessageViewHolderRight(var rightDataBinding: RightMessageLayoutBinding) :
        RecyclerView.ViewHolder(rightDataBinding.root){
            fun bind(messages: MessageModel){
                Log.d("MESSAGGESBIND",messages.message)
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

//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        TODO("Not yet implemented")
//    }

}