package com.project.messagingapp.ui.main.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.messagingapp.R


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