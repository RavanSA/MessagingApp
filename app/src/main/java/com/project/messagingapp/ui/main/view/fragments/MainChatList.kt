package com.project.messagingapp.ui.main.view.fragments

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
        getCurrentUserChatList()

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                // And Update Ui here
                getContactList()
            }
        }

        getContactList()
        return chatBinding.root
    }


    private fun getChatListRoom(): MutableList<ChatListRoom> {
        return chatListViewModel.getChatListRoom()
    }


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

    private fun getCurrentUserChatList() {
            chatListViewModel.currrentUserChatList.observe(requireActivity(), {
//                observeChatList(it)
                Log.d("TESTCURRENTYSERCHAT", it.toString())
                observeChatList(it.mainChatList)
            })
        }


    private fun observeChatList(chatListModel: List<ChatListModel>?) {
        if (chatListModel != null) {
            lifecycleScope.launch {
                chatListViewModel.getChatList(chatListModel)
                    .observe(requireActivity(), Observer { data ->
                        data?.let { remoteList ->
                            val testContactList = getContactList()
                            GlobalScope.launch {
                                testContactList.collect {
                            if(remoteList.size == it.size){
                                //DO NOTHING
                                Log.d("Test1","EQUAL")
                                Log.d("LOCAL", it.toString())
                                callAdapter(it)

                            } else if(remoteList.lastmessage == it.lastmessage) {
                                // if the last message not equal update it
                                //UPDATE LAST MESSAGE
                            } else {
                                //DELETE ALL CHAT
                                // CREATE ROOM CHAT LIST
                                Log.d("Test3","NOTEQUAL")
                                Log.d("REMOTE", remoteList.toString())
                                Log.d("LOCAL", it.toString())
                                //DELETE THEN ADD ALL
                                chatListViewModel.deleteChatList()
//                                newChatList.addAll(remoteList)
                                val newChat = mutableListOf<ChatListRoom>()
                                for(i in remoteList){
                                    val newChat = ChatListRoom(
                                        i.uid,i.chatid,i.message_date,i.lastMessageOfChat,i.receiverID
                                    )

                                    chatListViewModel.createChatIfNotExist(newChat)
                                }
                                callAdapter(remoteList)

                            }
                                    Log.d("REMOTE", remoteList.toString())
                                    Log.d("LOCAL", it.toString())
                                    Log.d("REMOTESIZE", remoteList.size.toString())
                                    Log.d("LOCALSIZE", it.size.toString())
                                }
                            }


                            Log.d("TESTCONTACTROOM", testContactList.toString())
                        }
                        Log.d("OBSERVECHATLIST", data.toString())
                    })
                //if last message not equal update room db last message
                // else if remote db.size != local db.size delete all create new chat or insert replace
                // else call adapter from local
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

}