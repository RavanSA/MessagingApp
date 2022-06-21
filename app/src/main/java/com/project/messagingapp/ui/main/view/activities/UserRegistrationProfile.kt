package com.project.messagingapp.ui.main.view.activities

import android.content.Context
import android.content.Intent
import org.tensorflow.lite.support.common.FileUtil
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Rect
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.project.messagingapp.BuildConfig
import com.project.messagingapp.data.model.UserRoomModel
import com.project.messagingapp.ui.main.viewmodel.RegistrationViewModel
import com.project.messagingapp.ui.main.viewmodel.UserRegistrationViewModel
import com.project.messagingapp.utils.AgeDetection
import com.project.messagingapp.utils.AppUtil
import kotlinx.android.synthetic.main.activity_user_registration_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.File
import java.lang.Math.floor
import android.provider.MediaStore

import android.os.Build

import android.content.ContentResolver
import android.graphics.ImageDecoder
import java.lang.Exception


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
    private val shift = 5
    private lateinit var ageModelInterpreter: Interpreter
    private lateinit var genderModelInterpreter: Interpreter
    private lateinit var ageDetection: AgeDetection

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .build()
    private val firebaseFaceDetector = FaceDetection.getClient(realTimeOpts)


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

        coroutineScope.launch {
            initModels(options)
        }

        imgPickImage.setOnClickListener {
                SelectPicture()
            }

            UserProfileSave.setOnClickListener {
                if (CheckUserData()) {
                    image?.let { it1 -> userViewModel.UploadData(username,status, it1) }
                        val userRoom = UserRoomModel(firebaseAuth!!.uid!!,username
                            ,firebaseAuth!!.currentUser!!.phoneNumber!!,status, image.toString()
                        )

                    if ( !compatList.isDelegateSupportedOnThisDevice ){
                        Toast.makeText(this,"GPU acceleration is not available on this device",Toast.LENGTH_LONG).show()
                    }

                    registrationViewModel.insertUser(userRoom)


                    val bitmapForDetectAge = image?.let { it1 -> uriToBitmap(it1) }
                    bitmapForDetectAge?.let { it1 -> detectFaces(it1) }

                    startActivity(Intent(this@UserRegistrationProfile,
                        MainChatScreen::class.java))
                }
            }
        }

    private suspend fun initModels(options: Interpreter.Options) = withContext( Dispatchers.Default ) {
        ageModelInterpreter = Interpreter(FileUtil.loadMappedFile( applicationContext , "model_age_q.tflite"), options )
        withContext( Dispatchers.Main ) {
            ageDetection = AgeDetection().apply {
                interpreter = ageModelInterpreter
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
                buttonBackgroundColor = R.color.white,
            ) {  pickImages.launch("image/*")}
            .onNegative(
                "Take Picture",
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
        takePicture.launch(image)
    }

    val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            Glide.with(this).load(image).into(imgPickImage)
        }
    }

    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->

        uri?.let { it ->
            image=it
            Glide.with(this).load(it).into(imgPickImage)
        }
    }


    private fun detectFaces(image: Bitmap) {
        val inputImage = InputImage.fromBitmap(image, 0)
        firebaseFaceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                if ( faces.size != 0 ) {
                    coroutineScope.launch {
                        val age = ageDetection.predictAge(cropToBBox(image, faces[0].boundingBox))
                        val ageFirebaseValue = floor( age.toDouble() ).toInt().toString()
                        val databaseRef = FirebaseDatabase.getInstance()
                            .getReference("Users").child(AppUtil().getUID()!!)
                        val map = HashMap<String, Any>()
                        map["estimatedAge"] = ageFirebaseValue
                        databaseRef.updateChildren(map)
                    }
                }
                else {
                    Toast.makeText(this,"no faces were detected",Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        ageModelInterpreter.close()

    }

    private fun cropToBBox(image: Bitmap, bbox: Rect) : Bitmap {
        return Bitmap.createBitmap(
            image,
            bbox.left - 0 * shift,
            bbox.top + shift,
            bbox.width() + 0 * shift,
            bbox.height() + 0 * shift
        )
    }


    private fun uriToBitmap(imageUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        val contentResolver = contentResolver
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } else {
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }

            bitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }
}