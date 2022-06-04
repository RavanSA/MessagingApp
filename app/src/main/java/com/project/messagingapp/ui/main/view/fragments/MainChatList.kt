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


class MainChatList : Fragment() {

    private lateinit var chatBinding: FragmentMainChatListBinding
    private lateinit var chatListViewModel: ChatListViewModel
    private var chatAdapter: ChatListRecyclerAdapter? = null
    private var locationManager : LocationManager? = null

    //    private var testChatList:MutableList<ChatListRoom> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatBinding = FragmentMainChatListBinding.inflate(layoutInflater,container,false)

        chatListViewModel = ViewModelProvider(this)[ChatListViewModel::class.java]

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                // And Update Ui here
//                getContactList(
//            getContactListAndChatList()
                getCurrentUserChatList()
            }
        }

//        locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//         locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L,
//             0f, locationListener)
//        }

        Log.d("TESTjbhjhhjh", chatListViewModel.getContactChatUntilChanged().toString())

        lifecycle.coroutineScope.launch {
            chatListViewModel.getContacChattList().distinctUntilChanged().collect() {
                callAdapter(it)
            }

        }

//        getLastKnownLocation(requireContext())
        return chatBinding.root
    }

    private suspend fun getContactListAndChatList(): MutableList<ContactChatList> {

        val testContactList = chatListViewModel.getContactListRoom2()
        val testChatList = chatListViewModel.getChatListRoom()
        val newList = mutableListOf<ContactChatList>()

        Log.d("TEST", testContactList.size.toString())
        Log.d("TEST2", testChatList.size.toString())
        Log.d("TESTCHATLIST", testChatList.toString())
        GlobalScope.launch(Dispatchers.IO) {

            for (elementContact in testContactList) {
                for (elementChat in testChatList) {
                    withContext(Dispatchers.IO) {

                    Log.d("ELEMENCTCHAT", elementChat.uid)
                    Log.d("ELEMENTCONTACT", elementContact.receiverID)
                        if (elementContact.receiverID == elementChat.uid) {
//                    Log.d("FINAL TEST", testChatList.toString())

                        Log.d("CONTACTRECEIVERID",elementContact.receiverID)
                        Log.d("CHATLISTRECEVIER",elementChat.uid)
//                        Log.d("CHATLISTRECEVIER",elementChat.)
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
//            callAdapter(newList)
//            getCurrentUserChatList(newList)
        }


        Log.d("NEWLIST CREATED", newList.toString())
        return newList
    }


    private suspend fun getCurrentUserChatList() {
        val local = getContactListAndChatList()
        if (!isNetworkAvailable(requireContext())) {
//            callAdapter(local)
            Log.d("No INTERNET CONNECTION", local.toString())
        } else if(isNetworkAvailable(requireContext())){
//            callAdapter(local)
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
//                                        chatListViewModel.insertContactChatList(local)
//                                        chatListViewModel.createChatIfNotExist(chatList)
//                                    roomElement.lastMessageOfChat = element.lastMessageOfChat
                                    }
                                    chatListViewModel.deleteAndCreate(newLocalChatList)
//                                    val newLocalDBChatList = chatListViewModel.getChatListRoom()
                                    Log.d("INTERNET","CONNECTION")
//                                    callAdapter(local)
                                } else {
                                  for (element in remoteList){
                                      chatListViewModel.updateLastMessage(
                                          element.lastMessageOfChat,
                                          element.message_date,
                                          element.chatID
                                      )

//                                      chatListViewModel.contactLastMessageUpdate(
//                                          element.message_date,
//                                          element.lastMessageOfChat,
//                                          element.chatID
//                                      )
                                  }
//                                    callAdapter(local)
                                    ///
                                    Log.d("INTERNET IN ELSE","CONNECTION")
                                }
                                }
                            }
            }
        }
    }

    private fun callAdapter(chatModel: MutableList<ContactChatList>){
//
//        if(chatModel.isEmpty()){
//            chatBinding.mainChatListEmpty.show()
//        } else {
        Log.d("CHATMODELADAPGTER", chatModel.toString())
            activity?.runOnUiThread {
                chatBinding.recyclerViewChat.layoutManager = LinearLayoutManager(
                    requireActivity(),
                    LinearLayoutManager.VERTICAL, false
                )

//                chatBinding.recyclerViewChat.adapter = ChatListRecyclerAdapter(chatModel){ item ->
//                    chatListViewModel.blockUser(item.)
//                }

                chatAdapter = context?.let { ChatListRecyclerAdapter(it,chatListViewModel,chatModel) }
                chatBinding.recyclerViewChat.adapter = chatAdapter
                chatAdapter!!.notifyDataSetChanged()
            }
//        }
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

//    private val locationListener: LocationListener = object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            val text = "$location.longitude:$location.latitude"
//            Log.d("LOC LAT", text)
//        }
//        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//        override fun onProviderEnabled(provider: String) {}
//        override fun onProviderDisabled(provider: String) {}
//    }
//fun getLastKnownLocation(context: Context) {
//    Log.d("LOCATION","FUNCTİON")
//    val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    val providers: List<String> = locationManager.getProviders(true)
//    var location: Location? = null
//    Log.d("PROVIDERSIZE", providers.size.toString())
//    for (i in providers.size - 1 downTo 0) {
//        Log.d("LOCATION","IN FOR LOOP")
//
//        if (ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) !==
//            PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                ActivityCompat.requestPermissions(requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
//            } else {
//                ActivityCompat.requestPermissions(requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
//            }
//        }
//
//        location= locationManager.getLastKnownLocation(providers[i])
//        if (location != null)
//            Log.d("LOCATIONGETTINGNULL", location.toString())
//        break
//    }
//    Log.d("LOGHERELOCATION", location.toString())
//    val gps = DoubleArray(2)
//    if (location != null) {
//        Log.d("LOCATION NOT NULL","ASAAA")
//        gps[0] = location.getLatitude()
//        gps[1] = location.getLongitude()
//        Log.e("gpsLat",gps[0].toString())
//        Log.e("gpsLong",gps[1].toString())
//    }
//
//}




}

