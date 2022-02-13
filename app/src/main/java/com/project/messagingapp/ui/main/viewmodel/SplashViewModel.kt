package com.project.messagingapp.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.concurrent.schedule

class SplashViewModel: ViewModel() {
    val openAct = MutableLiveData<Boolean>()
    private var timer: TimerTask? = null


    fun prepareTime() {
        if (timer == null) {
            timer = Timer().schedule(2000) {
                openAct.postValue(true)
            }
        }
    }
} 