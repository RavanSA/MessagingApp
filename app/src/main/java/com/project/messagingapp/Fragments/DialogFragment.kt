package com.project.messagingapp.Fragments

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.awesomedialog.*
import com.project.messagingapp.R
import kotlinx.android.synthetic.main.activity_user_registration_profile.*


class DialogFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_dialog, container, false)
    }
//    private val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
//
//        uri?.let { it ->
//            image=it
//            Glide.with(this).load(image).into(temp_prof_image)
//        }
//    }
}