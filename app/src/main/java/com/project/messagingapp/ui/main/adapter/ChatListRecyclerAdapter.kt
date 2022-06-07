package com.project.messagingapp.ui.main.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.messagingapp.R
import com.project.messagingapp.data.model.ChatListRoom
import com.project.messagingapp.data.model.ChatModel
import com.project.messagingapp.data.model.ContactChatList
import com.project.messagingapp.databinding.ChatlistItemLayoutBinding
import com.project.messagingapp.ui.main.view.activities.MessageActivity
import com.project.messagingapp.ui.main.viewmodel.ChatListViewModel
import com.project.messagingapp.utils.AES
import kotlinx.android.synthetic.main.chatlist_item_layout.*
import kotlinx.android.synthetic.main.chatlist_recycler_dialog.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.flow.Flow


class ChatListRecyclerAdapter(
    private val context: Context,
    private val chatListViewModel: ChatListViewModel,
    private val chatModel: MutableList<ContactChatList>
) : RecyclerView.Adapter<ChatListRecyclerAdapter.ChatListRecyclerView>(),
    View.OnLongClickListener {

    private lateinit var dialog: AlertDialog


    private fun getColor(): String{
        return chatListViewModel.getEmotion()
    }
//
//    fun inflateDialog (v: Context?) {
//        Toast.makeText(v, "long click", Toast.LENGTH_SHORT).show()
//        val updateDialog = AlertDialog.Builder(v)
//        val layout:View = LayoutInflater.from(v).inflate(R.layout.chatlist_recycler_dialog,
//            null,false)
//        updateDialog.setView(layout)
//
//        dialog = updateDialog.create()
//        dialog.show()
//
//
//        layout.block_user.setOnClickListener {
//            Toast.makeText(v?.context, "long click", Toast.LENGTH_SHORT).show()
//            dialog.dismiss()
//        }
//
//
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListRecyclerView {
        val chatItemLayoutBing: ChatlistItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.chatlist_item_layout,parent,false
        )
        return ChatListRecyclerView(chatItemLayoutBing)
    }

    override fun onBindViewHolder(holder: ChatListRecyclerView, position: Int) {
        val chatList = chatModel[position]
        holder.list.chatModel = chatList
        val regex = Regex("/^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)\$/gm")
        Log.d("REGEXMEATCHES", chatList.lastMessageOfChat.trim().contains("/^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)\$/gm".toRegex()).toString())

        val colorStr: String = getColor()
        if(colorStr.isNullOrEmpty()){
            holder.list.profImageChatList.borderColor = colorStr.toColorInt()
        }

        if("https://firebasestorage.googleapis.com/" in chatList.lastMessageOfChat) {
            holder.list.txtChatStatus.text = chatList.lastMessageOfChat
        } else {
            Log.d("AESDECRYPT",chatList.lastMessageOfChat)
            holder.list.txtChatStatus.text = AES.decrypt(chatList.lastMessageOfChat)
        }
        if(!chatList.receiver_image.isEmpty()) {
            Glide.with(holder.itemView.context).load(chatList.receiver_image)
                .into(holder.list.profImageChatList)
        }

//        holder.list.txtChatStatus.text = chatList.lastMessageOfChat
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, MessageActivity::class.java)
            intent.putExtra("id_receiver",chatList.receiver_id)
            intent.putExtra("chat_list_receiver_name",chatList.receiver_Name)
            intent.putExtra("chat_list_receiver_image", chatList.receiver_image)
            it.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            inflateDialog(chatList)
            return@setOnLongClickListener true
        }

    //        holder.itemView.setOnLongClickListener {
////            chatListViewModel.blockUser(chatList.receiver_id,"1")
//        }
    }

    override fun getItemCount(): Int {
        return chatModel.size
    }

    inner class ChatListRecyclerView(val list: ChatlistItemLayoutBinding )
        : RecyclerView.ViewHolder(list.root)

    fun inflateDialog(chatListItem: ContactChatList){

//            Toast.makeText(list, "long click", Toast.LENGTH_SHORT).show()
            val updateDialog = AlertDialog.Builder(context)
            val layout: View = LayoutInflater.from(context).inflate(
                R.layout.chatlist_recycler_dialog,
                null, false
            )
            updateDialog.setView(layout)

            dialog = updateDialog.create()
            dialog.show()


            layout.block_user.setOnClickListener {
                Toast.makeText(context, "${chatListItem.receiver_Name} blocked", Toast.LENGTH_SHORT).show()
                blockUser(chatListItem.receiver_id, "1")
                dialog.dismiss()
            }
    }

    fun blockUser(receiverID: String, block: String){
        return chatListViewModel.blockUser(receiverID, block)
    }

    override fun onLongClick(v: View?): Boolean {
        return true
    }

}