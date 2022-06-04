package com.project.messagingapp.ui.main.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.project.messagingapp.data.model.UserRoomModel
import com.project.messagingapp.ui.main.viewmodel.RegistrationViewModel
import com.project.messagingapp.ui.main.viewmodel.UserRegistrationViewModel
import com.project.messagingapp.utils.AgeDetection
import kotlinx.android.synthetic.main.activity_user_registration_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
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
    private lateinit var registrationViewModel: RegistrationViewModel
    private val compatList = CompatibilityList()
    private val coroutineScope = CoroutineScope( Dispatchers.Main )
    private lateinit var ageModelInterpreter: Interpreter
    private lateinit var genderModelInterpreter: Interpreter
    private lateinit var ageDetection: AgeDetection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration_profile)
        firebaseAuth = FirebaseAuth.getInstance()
        DatabaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance().reference
        userViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(UserRegistrationViewModel::class.java)

        registrationViewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        val options = Interpreter.Options().apply {
            addDelegate(GpuDelegate( compatList.bestOptionsForThisDevice ) )
        }

        imgPickImage.setOnClickListener {
                SelectPicture()
            }

            UserProfileSave.setOnClickListener {
                if (CheckUserData()) {
                    Log.d("INCHECKUSER", image.toString())
                    image?.let { it1 -> userViewModel.UploadData(username,status, it1) }
//                    val sharedPref : SharedPreferences = this@UserRegistrationProfile.
//                    getSharedPreferences("com.project.messaginapp.phonenumber", Context.MODE_PRIVATE)
//
//                    val number = sharedPref.getString("PhoneNumber",null )
                        val userRoom = UserRoomModel(firebaseAuth!!.uid!!,username
                            ,firebaseAuth!!.currentUser!!.phoneNumber!!,status, image.toString()
                        )

//                    registrationViewModel.updateUser(username,status)

                    if ( !compatList.isDelegateSupportedOnThisDevice ){
                        Toast.makeText(this,"GPU acceleration is not available on this device",Toast.LENGTH_LONG).show()
                    }

                    registrationViewModel.insertUser(userRoom)

                    coroutineScope.launch {
                        initModels(options)
                    }

                    startActivity(Intent(this@UserRegistrationProfile,
                        MainChatScreen::class.java))
                }
            }
        }

    private suspend fun initModels(options: Interpreter.Options) = withContext( Dispatchers.Default ) {
//        ageModelInterpreter = Interpreter(FileUtil.loadMappedFile( applicationContext , modelFilename[0]), options )
//        withContext( Dispatchers.Main ) {
//            ageEstimationModel = AgeEstimationModel().apply {
//                interpreter = ageModelInterpreter
//            }
//        }
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
                buttonBackgroundColor = R.color.white,
            ) {  pickImages.launch("image/*")}
            .onNegative( "Take Picture",
                buttonBackgroundColor = R.color.white,
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
            Glide.with(this).load(image).into(imgPickImage)
        }
    }

    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->

        uri?.let { it ->
            image=it
            Glide.with(this).load(it).into(imgPickImage)
        }
    }


//    private fun detectFaces(image: Bitmap) {
//        val inputImage = InputImage.fromBitmap(image, 0)
//        // Pass the clicked picture to MLKit's FaceDetector.
//        firebaseFaceDetector.process(inputImage)
//            .addOnSuccessListener { faces ->
//                if ( faces.size != 0 ) {
//                    // Set the cropped Bitmap into sampleImageView.
//                    sampleImageView.setImageBitmap(cropToBBox(image, faces[0].boundingBox))
//                    // Launch a coroutine
//                    coroutineScope.launch {
//
//                        // Predict the age and the gender.
//                        val age = ageEstimationModel.predictAge(cropToBBox(image, faces[0].boundingBox))
//                        val gender = genderClassificationModel.predictGender(cropToBBox(image, faces[0].boundingBox))
//
//                        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance()
//                            .getReference("Users").child(getUID()!!)
//                            .updateChildren(floor( age.toDouble() ).toInt().toString())
//
//                        // Show the final output to the user.
//
//                    }
//                }
//                else {
//                    // Show a dialog to the user when no faces were detected.
//                    Toast.makeText(this,"no faces were detected",Toast.LENGTH_LONG).show()
//                }
//            }
//    }

    override fun onDestroy() {
        super.onDestroy()
        ageModelInterpreter.close()

    }

//    private fun cropToBBox(image: Bitmap, bbox: Rect) : Bitmap {
//        return Bitmap.createBitmap(
//            image,
//            bbox.left - 0 * shift,
//            bbox.top + shift,
//            bbox.width() + 0 * shift,
//            bbox.height() + 0 * shift
//        )
//    }




}