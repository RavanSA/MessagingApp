package com.project.messagingapp.ui.main.view.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.project.messagingapp.R
import com.project.messagingapp.ui.main.viewmodel.SplashViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.project.messagingapp.data.model.ChatListRoom
import com.project.messagingapp.ui.main.viewmodel.ChatListViewModel
import com.project.messagingapp.utils.AppUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SplashScreen : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var chatListViewModel: ChatListViewModel
    private var testChatList:MutableList<ChatListRoom> = mutableListOf()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var gps: DoubleArray = doubleArrayOf(0.0,0.0)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        setContentView(R.layout.activity_splash_screen)

        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        splashViewModel.prepareTime()



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        splashViewModel.openAct.observe(this,
            {
                redirectUserTo()
            })
    }

    private fun redirectUserTo(){
        val notRegistratedUser = Intent(
            this@SplashScreen,
            RegistrationActivity::class.java
        )

        val registratedUser = Intent(
            this@SplashScreen,
            MainChatScreen::class.java
        )

        val sharedPref : SharedPreferences = this@SplashScreen.
        getSharedPreferences("com.project.messaginapp.phonenumber",Context.MODE_PRIVATE)

        val number = sharedPref.getString("PhoneNumber",null )

        if (number !== null){
            FirebaseApp.initializeApp(this@SplashScreen)
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        val token = it.result
                        val databaseReference =
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(AppUtil().getUID()!!)

                        val map: MutableMap<String, Any> = HashMap()
                        map["token"] = token!!
                        databaseReference.updateChildren(map)
                    }
                })

            chatListViewModel = ViewModelProvider(this)[ChatListViewModel::class.java]

            lifecycle.coroutineScope.launch {
                chatListViewModel.getChatListWithFlow().collect() {
                    testChatList = it
                    Log.d("TESTFLOW", it.toString())
                }
            }
            getLocation()
            startActivity(registratedUser)
        } else {
            startActivity(notRegistratedUser)
        }
    }

    fun getLocation() {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        val locationListener = android.location.LocationListener { location ->
            val latitute = location.latitude
            val longitute = location.longitude
            gps[0] = latitute
            gps[1] = longitute

            Log.i("AAAAAAAAAAAAAAAtest", "Latitute: $latitute ; Longitute: $longitute")
            AppUtil().updateUserLocation(latitute.toString(),longitute.toString())

            val sharedPref: SharedPreferences = this
                .getSharedPreferences("com.project.messaginapp.location",
                    Context.MODE_PRIVATE)
            val strLocation: String = gps[0].toString() + "," + gps[1].toString()
            val editor = sharedPref.edit()
            editor.putString("LOCATION",strLocation)
            editor.apply()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)

    }

    fun getCurrentUserLocation(): String {
        return gps[0].toString() + "," + gps[1].toString()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> Toast.makeText(this,"PERMISSOUN NOT GRANTED",Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}