package com.project.messagingapp.ui.main.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.ui.main.adapter.NearbyUsersAdapter
import com.project.messagingapp.ui.main.viewmodel.NearbyUsersViewModel
import kotlinx.android.synthetic.main.activity_nearby_user_fragment.*
import kotlinx.android.synthetic.main.nearby_user_item.*
import kotlinx.android.synthetic.main.toolbar_for_nearby.*
import kotlinx.android.synthetic.main.toolbar_message.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NearbyUserFragment : AppCompatActivity() {

    private lateinit var nearbyUserViewModel: NearbyUsersViewModel
    private var nearbyAdapter: NearbyUsersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_user_fragment)

        nearbyUserViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(NearbyUsersViewModel::class.java)

        val data = observeList()

        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Main){
                callAdapter(data)
            }
        }

        nearbyBack.setOnClickListener {
            val contactActivity = Intent(
                this,
                MainChatScreen::class.java
            )
            startActivity(contactActivity)
        }

    }

    private fun observeList(): MutableList<UserModel> {

        var nearbyUserList = mutableListOf<UserModel>()
        nearbyUserViewModel.getNearbyUsersList().observe(this,  { data ->
            nearbyUserList.addAll(data)
            countListNearby.text = nearbyUserList.size.toString() + " user found near you"
            if(data.size > 0){
                callAdapter(nearbyUserList)
            }
        })
        return nearbyUserList
    }

    @SuppressLint("NotifyDataSetChanged")
    fun callAdapter(data: MutableList<UserModel>) {

                nearbyAdapter = NearbyUsersAdapter(data)
        recyclerViewNearby.adapter = nearbyAdapter

        recyclerViewNearby.layoutManager = GridLayoutManager(
                    this@NearbyUserFragment, 2
                )
        recyclerViewNearby.hasFixedSize()
    }

}