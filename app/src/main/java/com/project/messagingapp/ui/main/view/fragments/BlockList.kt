package com.project.messagingapp.ui.main.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.messagingapp.R
import com.project.messagingapp.data.model.ContactChatList
import com.project.messagingapp.databinding.ActivityMessageBinding
import com.project.messagingapp.databinding.FragmentBlockListBinding
import com.project.messagingapp.ui.main.adapter.BlockListAdapter
import com.project.messagingapp.ui.main.adapter.MessageRecyclerAdapter
import com.project.messagingapp.ui.main.viewmodel.BlockListViewModel
import com.project.messagingapp.ui.main.viewmodel.MessageViewModel

class BlockList : Fragment() {

    private lateinit var blockListViewModel: BlockListViewModel
    private lateinit var blockListBinding: FragmentBlockListBinding
    private var blockListAdapter: BlockListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        blockListViewModel = ViewModelProvider(this)[BlockListViewModel::class.java]

        blockListBinding = FragmentBlockListBinding.inflate(layoutInflater)
        callBlockListAdapter()
        return blockListBinding.root
    }

    private fun getAllBlockedUser() : MutableList<ContactChatList>{
        return blockListViewModel.getAllBlockedUser()
    }

    private fun callBlockListAdapter(){
        val blockList = getAllBlockedUser()
        Log.d("BLOCKLIST", blockList.toString())
        blockListBinding.recyclerViewBlockList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        blockListAdapter = BlockListAdapter(blockListViewModel
                ,blockList)
        blockListBinding.recyclerViewBlockList.adapter = blockListAdapter

    }

}