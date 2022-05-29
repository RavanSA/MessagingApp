package com.project.messagingapp.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.messagingapp.R
import com.project.messagingapp.data.model.ChatModel
import com.project.messagingapp.data.model.ContactChatList
import com.project.messagingapp.databinding.ChatlistItemLayoutBinding
import com.project.messagingapp.ui.main.view.activities.MessageActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.flow.Flow

class ChatListRecyclerAdapter(
    private val chatModel: MutableList<ContactChatList>
) : RecyclerView.Adapter<ChatListRecyclerAdapter.ChatListRecyclerView>(){

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

        if(!chatList.receiver_image.isEmpty()) {
            Glide.with(holder.itemView.context).load(chatList.receiver_image)
                .into(holder.list.profImageChatList)
        }

//        holder.list.txtChatStatus.text = chatList.lastMessageOfChat
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, MessageActivity::class.java)
            intent.putExtra("id_receiver",chatList.receiver_id)
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return chatModel.size
    }

    inner class ChatListRecyclerView(val list: ChatlistItemLayoutBinding )
        : RecyclerView.ViewHolder(list.root)

}