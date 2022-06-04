package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.messagingapp.R
import com.project.messagingapp.ui.main.adapter.CustomSettingsAdapter
import com.project.messagingapp.ui.main.view.activities.UserProfile
import com.project.messagingapp.ui.main.view.activities.UserRegistrationProfile
import com.project.messagingapp.ui.main.viewmodel.RegistrationViewModel
import com.project.messagingapp.ui.main.viewmodel.SettingsViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_registration_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.settings_fragment.*
import java.io.File


const val PICK_PDF_FILE = 2

class SettingsFragment : Fragment(), SettingRecyclerClickListener {

    private lateinit var settingViewModel: SettingsViewModel
    private var settingRecyclerView: RecyclerView?= null
    private var customSettingAdapter: CustomSettingsAdapter ?= null
    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
            .create(SettingsViewModel::class.java)


        settingRecyclerView = settingRecycler

//        settingViewModel.getUserName().observe(viewLifecycleOwner, Observer { name ->
//            tvSettingUserName.text = name.name
//        })


 //       Glide.with(this@SettingsFragment).load(
   //         Uri.parse("//content://com.android.providers.media.documents/document/image%3A230391")).into(settingProfilePicture)

        settingViewModel.getArrayList().observe(viewLifecycleOwner, { settingViewModels ->
            customSettingAdapter = CustomSettingsAdapter(this,this,settingViewModels!!)
            settingRecyclerView!!.layoutManager = LinearLayoutManager(activity)
//content://com.android.providers.media.documents/document/image%3A230391
            //content://com.android.providers.media.documents/document/image%3A230391
            settingRecyclerView!!.adapter = customSettingAdapter
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        registrationViewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]


        registrationViewModel.getUserRoom().observe(viewLifecycleOwner, { user ->
//            pickImages.launch("image/*")
            Log.d("USERROOM", user.toString())
            tvSettingUserName.text = user.userName
            val settingProfileUri = Uri.parse(user.profilePhoto)
            Log.d("SETTİNGFRAGMENTURI", settingProfileUri.path.toString())
//            Picasso.with(context).load(settingProfileUri.path).into(settingProfilePicture)
            Glide.with(this).load(settingProfileUri)
                .error(R.drawable.profilepic)
                .centerCrop()
                .timeout(60000)
                .into(settingProfilePicture)
//                settingProfilePicture.setImageURI(settingProfileUri)

        })


        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onRecyclerViewItemClick(view: View, settingList: SettingsViewModel) {
          when(settingList.title){
            "Account" -> {

                startActivity(Intent(context, UserProfile::class.java))
                requireActivity().finish()
            }

            "Chat" -> {
                Toast.makeText(requireContext(),"CHAT", Toast.LENGTH_LONG).show()
            }
              "Block List" -> {
                  val transaction = activity?.supportFragmentManager?.beginTransaction()
                  transaction?.replace(R.id.setting_container, BlockList())
                  transaction?.disallowAddToBackStack()
                  transaction?.commit()
              }
            "Help" -> {
                Toast.makeText(requireContext(),"HELP", Toast.LENGTH_LONG).show()
            }
        }
    }


//
//    fun openFile(pickerInitialUri: Uri) {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/image"
//
//            // Optionally, specify a URI for the file that should appear in the
//            // system file picker when it loads.
//            putExtra(Pic.EXTRA_INITIAL_URI, pickerInitialUri)
//        }
//
//        startActivityForResult(intent, PICK_PDF_FILE)
//    }
//
//    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
//        uri?.let { it ->
//            Log.d("PICKIMAGESTORAGE", it.toString())
//            Glide.with(this).load(it).into(settingProfilePicture)
//        }
//    }

}

interface SettingRecyclerClickListener{
    fun onRecyclerViewItemClick(view: View, settingList: SettingsViewModel)
}