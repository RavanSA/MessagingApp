package com.project.messagingapp.ui.main.view.activities

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
import com.project.messagingapp.utils.OnClickInterface
import kotlinx.android.synthetic.main.dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.pick_image_dialog.view.*
import java.io.File

class UserProfile : AppCompatActivity(),OnClickInterface {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var profileBinding: ActivityUserProfileBinding
    private lateinit var pickImages: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private var imageURI: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)


        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]


        profileViewModel.getUser().observe(this, { userModel ->
            profileBinding.userModel = userModel
            ProfileFirstName.text = userModel.name
            ProfilePhone.text =  userModel.number.toString()
            ProfileStatus.text =  userModel.status
        })

        pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
            uri?.let { it ->
                profileViewModel.updateImage(imageURI!!)
                Glide.with(this).load(it).into(UpdateProfilePic)
            }
        }

        imageURI = takePicture()!!

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            Toast.makeText(this, "In Launcher" + imageURI.toString(),Toast.LENGTH_LONG).show()
            if (success) {
                profileViewModel.updateImage(imageURI!!)
                Glide.with(this).load(imageURI).into(UpdateProfilePic)
            }
        }

        profileBinding.updatePickImage.setOnClickListener {
            Toast.makeText(this, imageURI.toString(),Toast.LENGTH_LONG).show()
            updateImageDialog()
        }


        profileBinding.clickHandler = this

    }


    private fun updateStatusAlertDialog(){
        Toast.makeText(this,"DIALOG WORKED", Toast.LENGTH_LONG).show()

        val updateDialog = AlertDialog.Builder(this)
        val layout:View = LayoutInflater.from(this).inflate(R.layout.dialog_layout,
            null,false)
        updateDialog.setView(layout)

        layout.btnEdit.setOnClickListener {
            val status: String = layout.updateDialog.text.toString()
            if(status.isNotEmpty()){
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

            pickImages.launch("image/*")
            Toast.makeText(this,"IN CLICK LSİTENR: $imageURI",Toast.LENGTH_LONG).show()
            profileViewModel.updateImage(imageURI!!)

            dialog.dismiss()

        }

        layout.imageFromStorage.setOnClickListener {
            takePictureLauncher.launch(imageURI!!)
            Toast.makeText(this,"IN CLCICK LSİTENR: $imageURI",Toast.LENGTH_LONG).show()
            profileViewModel.updateImage(imageURI!!)

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

    private fun takePicture(): Uri? {
        val root =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID + File.separator)
        root.mkdirs()
        val fname = "img_" + System.currentTimeMillis() + ".jpg"
        val sdImageMainDirectory = File(root, fname)

        val image = FileProvider.getUriForFile(this@UserProfile, "com.project.messagingapp.provider",
            sdImageMainDirectory)
        Log.d("IMAGEINCAMERA", image.toString())

        return image
    }

}