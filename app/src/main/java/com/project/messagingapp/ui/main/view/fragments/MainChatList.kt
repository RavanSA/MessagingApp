package com.project.messagingapp.ui.main.view.fragments

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.api.LogDescriptor
import com.project.messagingapp.R
import com.project.messagingapp.data.model.*
import com.project.messagingapp.databinding.FragmentMainChatListBinding
import com.project.messagingapp.ui.main.adapter.ChatListRecyclerAdapter
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter
import com.project.messagingapp.ui.main.viewmodel.ChatListViewModel
import com.project.messagingapp.ui.main.viewmodel.ContactInfoViewModel
import com.project.messagingapp.utils.AppUtil
import io.ak1.pix.helpers.show
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.forEach
import com.iceteck.silicompressorr.videocompression.MediaController.mContext
import com.project.messagingapp.databinding.ChatlistItemLayoutBinding
import kotlinx.android.synthetic.main.chatlist_item_layout.*


class MainChatList : Fragment() {

    private lateinit var chatBinding: FragmentMainChatListBinding
    private lateinit var chatListViewModel: ChatListViewModel
    private var chatAdapter: ChatListRecyclerAdapter? = null
    private var locationManager : LocationManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatBinding = FragmentMainChatListBinding.inflate(layoutInflater,container,false)


        chatListViewModel = ViewModelProvider(this)[ChatListViewModel::class.java]

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                getCurrentUserChatList()
            }
        }

        lifecycle.coroutineScope.launch {
            chatListViewModel.getContacChattList().distinctUntilChanged().collect() {
                callAdapter(it)
            }

        }

        return chatBinding.root
    }

    private suspend fun getContactListAndChatList(): MutableList<ContactChatList> {

        val testContactList = chatListViewModel.getContactListRoom2()
        val testChatList = chatListViewModel.getChatListRoom()
        val newList = mutableListOf<ContactChatList>()

        GlobalScope.launch(Dispatchers.IO) {

            for (elementContact in testContactList) {
                for (elementChat in testChatList) {
                    withContext(Dispatchers.IO) {

                        if (elementContact.receiverID == elementChat.uid) {
                        val contactList = ContactChatList(
                            elementChat.chatID,
                            elementContact.receiverID,
                            elementContact.name,
                            elementContact.number,
                            elementContact.status,
                            elementContact.image,
                            "",
                            elementChat.date,
                            elementChat.lastMessage
                        )

                            chatListViewModel.insertContactChatList(contactList)

                            newList.add(contactList)
                    }
                    }
                    }
            }
        }

        return newList
    }


    private suspend fun getCurrentUserChatList() {
        val local = getContactListAndChatList()
        if (!isNetworkAvailable(requireContext())) {
            Log.d("No INTERNET CONNECTION", local.toString())
        } else if(isNetworkAvailable(requireContext())){
            chatListViewModel.currrentUserChatList.observe(requireActivity(), {
                Log.d("INTERNET CONNECTION", it.toString())
                observeChatList(it.mainChatList,local)
            })
        }
    }

    private fun observeChatList(
        chatListModel: List<ChatListModel>?,
        local: MutableList<ContactChatList>
    ) {
        if (chatListModel != null) {
            lifecycleScope.launch {
                val data = chatListViewModel.getChatList(chatListModel)
                        data?.let { remoteList ->
                            GlobalScope.launch(Dispatchers.IO) {
                                if(remoteList.size != local.size) {

                                    var newLocalChatList = mutableListOf<ChatListRoom>()
                                    for (element in remoteList) {
//                                    for(roomElement in local)
                                        val chatList = ChatListRoom(
                                            element.receiver_id,
                                            element.chatID,
                                            element.message_date,
                                            element.lastMessageOfChat,
                                            element.uid
                                        )
                                        newLocalChatList.add(chatList)
                                    }
                                    chatListViewModel.deleteAndCreate(newLocalChatList)
                                    Log.d("INTERNET","CONNECTION")
                                } else {
                                  for (element in remoteList){
                                      chatListViewModel.updateLastMessage(
                                          element.lastMessageOfChat,
                                          element.message_date,
                                          element.chatID
                                      )

                                  }
                                }
                                }
                            }
            }
        }
    }

    private fun callAdapter(chatModel: MutableList<ContactChatList>){
            activity?.runOnUiThread {
                chatBinding.recyclerViewChat.layoutManager = LinearLayoutManager(
                    requireActivity(),
                    LinearLayoutManager.VERTICAL, false
                )

                chatAdapter = context?.let { ChatListRecyclerAdapter(it,chatListViewModel,chatModel) }
                chatBinding.recyclerViewChat.adapter = chatAdapter
                chatAdapter!!.notifyDataSetChanged()
            }

    }


    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}

