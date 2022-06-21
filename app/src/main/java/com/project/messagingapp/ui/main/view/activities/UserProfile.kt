package com.project.messagingapp.ui.main.view.activities

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.messagingapp.BuildConfig
import com.project.messagingapp.R
import com.project.messagingapp.databinding.ActivityUserProfileBinding
import com.project.messagingapp.ui.main.viewmodel.ProfileViewModel
import com.project.messagingapp.ui.main.viewmodel.RegistrationViewModel
import com.project.messagingapp.utils.AppUtil
import com.project.messagingapp.utils.OnClickInterface
import kotlinx.android.synthetic.main.dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.pick_image_dialog.view.*
import kotlinx.android.synthetic.main.settings_fragment.*
import java.io.File

class UserProfile : AppCompatActivity(),OnClickInterface {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var profileBinding: ActivityUserProfileBinding
    private var imageURI: Uri? = null
    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)

        registrationViewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        registrationViewModel.getUserRoom().observe(this, { user ->
            ProfileFirstName.text = user.userName
            ProfilePhone.text =  user.phoneNumber
            ProfileStatus.text =  user.userStatus
            val roomUri = Uri.parse(user.profilePhoto)
            Glide.with(this).load(roomUri).into(updatePickImage)
        })

        profileBinding.updatePickImage.setOnClickListener {
            updateImageDialog()
        }

        profileBinding.clickHandler = this

    }


    private fun updateStatusAlertDialog(){

        val updateDialog = AlertDialog.Builder(this)
        val layout:View = LayoutInflater.from(this).inflate(R.layout.dialog_layout,
            null,false)
        updateDialog.setView(layout)

        layout.btnEdit.setOnClickListener {
            val status: String = layout.updateDialog.text.toString()
            if(status.isNotEmpty()){
                registrationViewModel.updateUserLocalStatus(status)
                profileViewModel.updateStatus(status)
                dialog.dismiss()
            }
        }
        dialog = updateDialog.create()
        dialog.show()
    }


    private fun updateNameAlertDialog() {
        val updateDialog = AlertDialog.Builder(this)
        val layout:View = LayoutInflater.from(this).inflate(R.layout.dialog_layout,
            null,false)
        updateDialog.setView(layout)

        layout.btnEdit.setOnClickListener {
            val name: String = layout.updateDialog.text.toString()
            if(name.isNotEmpty()){
                registrationViewModel.updateUserLocalName(name)
                profileViewModel.updateName(name)
                dialog.dismiss()
            }
        }
        dialog = updateDialog.create()
        dialog.show()
    }

    private fun updateImageDialog(){

        val updateDialog = AlertDialog.Builder(this)
        val layout:View = LayoutInflater.from(this).inflate(R.layout.pick_image_dialog,
            null,false)
        updateDialog.setView(layout)

        layout.imageFromCamera.setOnClickListener {
            requestPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )

            dialog.dismiss()
        }

        layout.imageFromStorage.setOnClickListener {
            pickImages.launch("image/*")
            dialog.dismiss()
        }

        dialog = updateDialog.create()
        dialog.show()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.ProfileStatus -> {
                updateStatusAlertDialog()
            }

            R.id.ProfileFirstName  -> {
                updateNameAlertDialog()
            }

            R.id.ProfilePhone -> {
                Toast.makeText(this,"Phone number can not changable",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun takePicture() {
        val root =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID + File.separator)
        root.mkdirs()
        val fname = "img_" + System.currentTimeMillis() + ".jpg"
        val sdImageMainDirectory = File(root, fname)

        imageURI = FileProvider.getUriForFile(this@UserProfile, "com.project.messagingapp.provider",
            sdImageMainDirectory)
        takePictureLauncher.launch(imageURI)
    }

    val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        Toast.makeText(this, "In Launcher" + imageURI.toString(),Toast.LENGTH_LONG).show()
        if (success) {
            registrationViewModel.updatedUserLocalProfileImage(imageURI.toString())
            profileViewModel.updateImage(imageURI!!)
            Glide.with(this).load(imageURI).into(updatePickImage)
        }
    }

    override fun onResume() {
        super.onResume()
        AppUtil().updateOnlineStatus("online")
    }

    override fun onPause() {
        super.onPause()
        AppUtil().updateOnlineStatus("offline")
    }

    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
        uri?.let { it ->
            registrationViewModel.updatedUserLocalProfileImage(it.toString())
            profileViewModel.updateImage(it)
            Glide.with(this).load(it).into(updatePickImage)
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                takePicture()
            } else {
                Toast.makeText(this,"GİVE PERMISSION TO THE USER",Toast.LENGTH_SHORT).show()
            }
        }

}