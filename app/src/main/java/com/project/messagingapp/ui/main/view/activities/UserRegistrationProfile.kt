package com.project.messagingapp.ui.main.view.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.project.messagingapp.R
import com.example.awesomedialog.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.project.messagingapp.BuildConfig
import com.project.messagingapp.ui.main.viewmodel.UserRegistrationViewModel
import kotlinx.android.synthetic.main.activity_user_registration_profile.*
import java.io.File

class UserRegistrationProfile : AppCompatActivity() {

    private var image: Uri? = null
    private lateinit var username: String
    private lateinit var status: String
    private var DatabaseRef: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageRef: StorageReference? = null
    private lateinit var ImageUrl: String
    private lateinit var userViewModel: UserRegistrationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration_profile)
        firebaseAuth = FirebaseAuth.getInstance()
        DatabaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance().reference
        userViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(UserRegistrationViewModel::class.java)


        imgPickImage.setOnClickListener {
                SelectPicture()
            }

            UserProfileSave.setOnClickListener {
                if (CheckUserData()) {
                    Log.d("INCHECKUSER", image.toString())
                    image?.let { it1 -> userViewModel.UploadData(username,status, it1) }
                    startActivity(Intent(this@UserRegistrationProfile,
                        MainChatScreen::class.java))
                }
            }
        }

    fun SelectPicture() {
        AwesomeDialog.build(this)
            .title(
                "Choose Profile Picture",
            )
            .body(
                "Take Picture Or From Gallery",
            )
            .icon(R.drawable.ic_baseline_person_add_24)
            .background(R.color.BackGround)
            .onPositive(
                "From Files",
                buttonBackgroundColor = R.color.WHITE,
            ) {  pickImages.launch("image/*")}
            .onNegative( "Take Picture",
                buttonBackgroundColor = R.color.WHITE,
            ){takePicture()}
    }


    private fun CheckUserData(): Boolean {
        username = edtProfileName.text.toString().trim()
        status = edtProfileStatus.text.toString().trim()

        if (username.isEmpty()) {
            edtProfileName.error = "Username cannot be empty"
            return false
        } else if (status.isEmpty()) {
            edtProfileStatus.error = "Status can not be empty"
            return false
        } else if (image == null) {
            Toast.makeText(this@UserRegistrationProfile, "Image required", Toast.LENGTH_SHORT).show()
            return false
        } else return true
    }

    fun takePicture() {
        val root =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID + File.separator)
        root.mkdirs()
        val fname = "img_" + System.currentTimeMillis() + ".jpg"
        val sdImageMainDirectory = File(root, fname)
        image = FileProvider.getUriForFile(this@UserRegistrationProfile, "com.project.messagingapp.provider",
            sdImageMainDirectory)
        Log.d("IMAGEINCAMERA", image.toString())
        takePicture.launch(image)
    }

    val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            Log.d("IMAGEINLAUNCHER", image.toString())
            Glide.with(this).load(image).into(temp_prof_image)
        }
    }

    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->

        uri?.let { it ->
            image=it
            Glide.with(this).load(it).into(temp_prof_image)
        }
    }

}