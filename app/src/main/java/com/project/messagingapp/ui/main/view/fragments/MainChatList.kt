package com.project.messagingapp.ui.main.view.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.LogDescriptor
import com.project.messagingapp.R
import com.project.messagingapp.data.model.*
import com.project.messagingapp.databinding.FragmentMainChatListBinding
import com.project.messagingapp.ui.main.adapter.ChatListRecyclerAdapter
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter
import com.project.messagingapp.ui.main.viewmodel.ChatListViewModel
import com.project.messagingapp.ui.main.viewmodel.ContactInfoViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach


class MainChatList : Fragment() {

    private lateinit var chatBinding: FragmentMainChatListBinding
    private lateinit var chatListViewModel: ChatListViewModel
    private var chatAdapter: ChatListRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatBinding = FragmentMainChatListBinding.inflate(layoutInflater,container,false)

        chatListViewModel = ViewModelProvider(this)[ChatListViewModel::class.java]
//        getCurrentUserChatList()

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                // And Update Ui here
//                getContactList(
//            getContactListAndChatList()
                getCurrentUserChatList()
            }
        }

//        getContactListAndChatList()
//        getContactList()
        return chatBinding.root
    }


//    private fun getChatListRoom(): MutableList<ChatListRoom> {
//        return chatListViewModel.getChatListRoom()
//    }


    private fun getContactList(): Flow<MutableList<ContactChatList>> {
        var contactList = chatListViewModel.getContactListRoom()
        GlobalScope.launch {
            chatListViewModel.getContactListRoom().collect {
              callAdapter(it)
            }
        }
//        callAdapter(testContactList)
        return contactList
    }

    private suspend fun getContactListAndChatList(): MutableList<ContactChatList> {
        val testContactList = chatListViewModel.getContactListRoom2()
        val testChatList = chatListViewModel.getChatListRoom()
        val newList = mutableListOf<ContactChatList>()

        Log.d("TEST", testContactList.size.toString())
        Log.d("TEST2", testChatList.size.toString())
        GlobalScope.launch(Dispatchers.IO) {

            for (elementContact in testContactList) {
                for (elementChat in testChatList) {
                    withContext(Dispatchers.IO) {

                    Log.d("ELEMENCTCHAT", elementChat.member)
                    Log.d("ELEMENTCONTACT", elementContact.receiverID)
                        if (elementContact.receiverID == elementChat.member) {
//                    Log.d("FINAL TEST", testChatList.toString())

                        Log.d("CONTACTRECEIVERID",elementContact.receiverID)
                        Log.d("CHATLISTRECEVIER",elementChat.uid)
//                        Log.d("CHATLISTRECEVIER",elementChat.)
                        val contactList = ContactChatList(
                            elementContact.receiverID,
                            elementContact.name,
                            elementContact.number,
                            elementContact.status,
                            elementContact.image,
                            "",
                            elementChat.chatID,
                            elementChat.date,
                            elementChat.lastMessage
                        )
                        newList.add(contactList)
                    }
                    }
                    }
            }
//            callAdapter(newList)
//            getCurrentUserChatList(newList)
        }
        Log.d("NEWLIST CREATED", newList.toString())
        return newList
    }


    private suspend fun getCurrentUserChatList() {
        var local = getContactListAndChatList()
        if (!isNetworkAvailable(requireContext())) {
            callAdapter(local)
            Log.d("No INTERNET CONNECTION", local.toString())
        } else if(isNetworkAvailable(requireContext())){
            callAdapter(local)
            chatListViewModel.currrentUserChatList.observe(requireActivity(), {
//                observeChatList(it)
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
//                                    chatListViewModel.deleteChatList()
                                    var newLocalChatList = mutableListOf<ChatListRoom>()
                                    for (element in remoteList) {
//                                    for(roomElement in local)
                                        val chatList = ChatListRoom(
                                            element.receiver_id,
                                            element.chatid,
                                            element.message_date,
                                            element.lastMessageOfChat,
                                            element.uid
                                        )
                                        newLocalChatList.add(chatList)
//                                        chatListViewModel.createChatIfNotExist(chatList)
//                                    roomElement.lastMessageOfChat = element.lastMessageOfChat
                                    }
                                    chatListViewModel.deleteAndCreate(newLocalChatList)
//                                    val newLocalDBChatList = chatListViewModel.getChatListRoom()
                                    Log.d("INTERNET","CONNECTION")
                                    callAdapter(local)
                                } else {
                                  for (element in remoteList){
                                      chatListViewModel.updateLastMessage(
                                          element.lastMessageOfChat,
                                          element.message_date,
                                          element.chatid
                                      )
                                  }
                                    callAdapter(local)
                                    ///
                                    Log.d("INTERNET IN ELSE","CONNECTION")
                                }
                                }
                            }


            }
        }
    }

    private fun callAdapter(chatModel: MutableList<ContactChatList>){
        activity?.runOnUiThread{
            chatBinding.recyclerViewChat.layoutManager = LinearLayoutManager(requireActivity(),
                LinearLayoutManager.VERTICAL,false)
            chatAdapter = ChatListRecyclerAdapter(chatModel)
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