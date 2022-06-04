package com.project.messagingapp.ui.main.adapter


import android.annotation.SuppressLint
import android.content.Intent
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.databinding.ContactItemBinding
import androidx.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.project.messagingapp.data.model.ContactChatList
import com.project.messagingapp.databinding.BlockListItemBinding
import com.project.messagingapp.ui.main.view.activities.ContactUserInfo
import com.project.messagingapp.ui.main.view.activities.MessageActivity
import com.project.messagingapp.ui.main.view.fragments.VerifyNum
import com.project.messagingapp.ui.main.viewmodel.BlockListViewModel
import java.util.*
import kotlin.collections.ArrayList

class BlockListAdapter(
    private val blockListViewModel: BlockListViewModel,
    private val blockList: MutableList<ContactChatList>
) : RecyclerView.Adapter<BlockListAdapter.BlockListView>() {

//    var appContacts: List<UserModel> = appUserContacts
//    private var allContact: List<UserModel> = appContacts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BlockListAdapter.BlockListView {
        val layoutInflater = LayoutInflater.from(parent.context)

        val blockItemBinding: BlockListItemBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.block_list_item, parent,false)

        return BlockListView(blockItemBinding)
    }

    override fun onBindViewHolder(holder: BlockListAdapter.BlockListView, position: Int) {
        val blockList = blockList[position]

        holder.item.userItem = blockList

        if(blockList.receiver_image.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(blockList.receiver_image)
                .into(holder.item.imageProfile)
        }

        Log.d("IN ADAPTER","TRUE")
        holder.itemView.setOnClickListener {
            blockListViewModel.removeBlock(blockList.receiver_id,"0")
            Toast.makeText(it.context," You removed ${blockList.receiver_Name}'s block ",Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return blockList.size
    }


    inner class BlockListView(val item: BlockListItemBinding) : RecyclerView.ViewHolder(item.root)


}
