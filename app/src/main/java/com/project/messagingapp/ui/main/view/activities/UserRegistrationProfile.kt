package com.project.messagingapp.ui.main.view.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.project.messagingapp.constants.AppConstants
import com.project.messagingapp.R
import com.example.awesomedialog.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.project.messagingapp.BuildConfig
import kotlinx.android.synthetic.main.activity_user_registration_profile.*
import java.io.File


class UserRegistrationProfile : AppCompatActivity() {

//    private val CropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
//        override fun createIntent(context: Context, input: Any?): Intent {
//            Toast.makeText(context, "TESTING CONTRACT", Toast.LENGTH_LONG).show()
//
//            return CropImage
//                .activity()
//                .getIntent(this@UserRegistrationProfile)
//        }
//
//        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
//            Toast.makeText(this@UserRegistrationProfile, "TESTING PARSE RESULT ",Toast.LENGTH_LONG).show()
//            return CropImage.getActivityResult(intent)?.originalUri
//
//        }
//    }

    private var image: Uri? = null
    private lateinit var username: String
    private lateinit var status: String
    private var DatabaseRef: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageRef: StorageReference? = null
    private lateinit var ImageUrl: String
    private var IMAGEuri: String = "not changed"
    private lateinit var CropActivityResultLauncher: ActivityResultLauncher<Any?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration_profile)
        firebaseAuth = FirebaseAuth.getInstance()
        DatabaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance().reference
//        CropActivityResultLauncher = registerForActivityResult(CropActivityResultContract) {
//
//            it?.let { originalUri ->
//                temp_prof_image.setImageURI(originalUri)
//                image = originalUri
//                Log.d("LAUNCHER WORKED", originalUri.toString())
//            }
//        }

            imgPickImage.setOnClickListener {
//                CropActivityResultLauncher.launch(null)
//                pickImages.launch("image/*")
//                takePicture()
                SelectPicture()
                Log.d("LAUNCHERRRRR OM", image.toString())
            }

            UserProfileSave.setOnClickListener {
                if (CheckUserData()) {
                    Log.d("INCHECKUSER", image.toString())
                    UploadData(username, status, image!!)
                }
            }

        }

    private fun SelectPicture() {
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

        Log.d("USERNAME:", username)
        Log.d("STATUS:", status)
        Log.d("IMAGE:", image.toString())
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

    private fun UploadData(username: String, status: String, image: Uri) = kotlin.run {
        firebaseAuth!!.uid?.let {
            storageRef!!.child(AppConstants.Path).child(it).putFile(image)
                .addOnSuccessListener {
                    val task = it.storage.downloadUrl
                    task.addOnCompleteListener { uri ->
                        ImageUrl = uri.result.toString()
                        val map = mapOf(
                            "name" to username,
                            "status" to status,
                            "image" to ImageUrl
                        )

                        Log.d("FAILURELISTENER",map.toString())
                        DatabaseRef!!.child(firebaseAuth!!.uid!!).updateChildren(map)
                        startActivity(Intent(this@UserRegistrationProfile, MainChatScreen::class.java))
                    }
                    task.addOnFailureListener { exception ->
                        Log.d("FAILURELISTENER", exception.toString())
                    }
                }
        }
    }

    fun takePicture() {
        val root =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID + File.separator)
        root.mkdirs()
        val fname = "img_" + System.currentTimeMillis() + ".jpg"
        val sdImageMainDirectory = File(root, fname)
//        val packageName = this@UserRegistrationProfile.applicationContext?.packageName
//        image = FileProvider.getUriForFile(this@UserRegistrationProfile,
//            "$packageName.provider", sdImageMainDirectory)
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
            Log.d("IMAGEINFILES", image.toString())
            Glide.with(this).load(it).into(temp_prof_image)
        }
    }


//    fun takePicture() {
//        val root =
//            File(Environment.getExternalStorageDirectory(), BuildConfig.APPLICATION_ID + File.separator)
//        root.mkdirs()
//        val fname = "img_" + System.currentTimeMillis() + ".jpg"
//        val file = File(root, fname)
//        image  = FileProvider.getUriForFile(requireContext(), context?.applicationContext?.packageName + ".provider", file)
//
//        takePicture.launch(image)
//    }
//
//    val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
//        if (success) {
//            // The image was saved into the given Uri -> do something with it
//            Glide.with(this).load(image).into(temp_prof_image)
//        }
//    }
}