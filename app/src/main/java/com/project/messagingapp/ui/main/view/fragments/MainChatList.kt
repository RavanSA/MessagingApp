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
import com.project.messagingapp.data.model.ChatListModel
import com.project.messagingapp.data.model.ChatListResponse
import com.project.messagingapp.data.model.ChatModel
import com.project.messagingapp.databinding.FragmentMainChatListBinding
import com.project.messagingapp.ui.main.adapter.ChatListRecyclerAdapter
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter
import com.project.messagingapp.ui.main.viewmodel.ChatListViewModel
import com.project.messagingapp.ui.main.viewmodel.ContactInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        return chatBinding.root
    }


    private fun getCurrentUserChatList(){
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
                        data?.let { callAdapter(it) }
                        Log.d("OBSERVECHATLIST", data.toString())
                    })
            }
        }
    }

    private fun callAdapter(chatModel:MutableList<ChatModel>){
        chatBinding.recyclerViewChat.layoutManager = LinearLayoutManager(requireActivity(),
            LinearLayoutManager.VERTICAL,false)
        chatAdapter = ChatListRecyclerAdapter(chatModel)
        chatBinding.recyclerViewChat.adapter = chatAdapter
        chatAdapter!!.notifyDataSetChanged()
    }
}
