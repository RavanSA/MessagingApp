package com.project.messagingapp.Fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.project.messagingapp.Activities.MainChatScreen
import com.project.messagingapp.Constants.AppConstants
import com.project.messagingapp.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_get_user_data.view.*

class GetUserData : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var image: Uri? = null
    private lateinit var username: String
    private lateinit var status: String
    private var DatabaseRef: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageRef: StorageReference? = null
    private lateinit var ImageUrl: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_get_user_data, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        DatabaseRef = FirebaseDatabase.getInstance().getReference("Users")
//        storageReference = FirebaseStorage.getInstance().reference
        view.UserProfileSave.setOnClickListener {
            if (CheckUserData()) {
                UploadData(username, status)
            }
        }

//        view.imgPickImage.setOnClickListener {
//            if (CheckStoragePermisson()) {
//                Toast.makeText(context, "CLICKED IF", Toast.LENGTH_LONG).show()
//                PickImage()
//            } else {
//                Toast.makeText(context, "CLICKED ELSE", Toast.LENGTH_LONG).show()
//                StorageRequestPermisson()
//            }
//        }


                view.imgPickImage.setOnClickListener {
                    Toast.makeText(context, "Profile Photo Upload is not implemented yet", Toast.LENGTH_LONG).show()

                }

        return view
    }

    private fun CheckUserData(): Boolean {
        username = requireView().edtProfileName.text.toString().trim()
        status = requireView().edtProfileStatus.text.toString().trim()
        Log.d("USERNAME:", username)
        Log.d("STATUS:", status)
        if (username.isEmpty()) {
            requireView().edtProfileName.error = "Username cannot be empty"
            return false
        } else if (status.isEmpty()) {
            requireView().edtProfileStatus.error = "Status can not be empty"
            return false
        } else if (image == null) {
            Toast.makeText(context, "Image required", Toast.LENGTH_SHORT).show()
            return true
        } else return true
    }

    private fun UploadData(username: String, status: String) = kotlin.run {
//        storageRef!!.child(firebaseAuth!!.uid + AppConstants.Path).putFile(image)
//            .addOnSuccessListener {
//                val task = it.storage.downloadUrl
//                task.addOnCompleteListener { uri ->
//                    ImageUrl = uri.result.toString()
                    val map = mapOf(
                        "name" to username,
                        "status" to status,
                        "image" to "empty"
                    )
                    DatabaseRef!!.child(firebaseAuth!!.uid!!).updateChildren(map)
                    startActivity(Intent(context, MainChatScreen::class.java))
                    requireActivity().finish()
                }
            }



//    private fun CheckStoragePermisson(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//
//    }
//
//    private fun StorageRequestPermisson() =
//        ActivityCompat.requestPermissions(
//            requireActivity(), arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ), 1000
//        )
//
////    override fun onRequestPermissionsResult(
////        requestCode: Int,
////        permissions: Array<out String>,
////        grantResults: IntArray
////    ) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
////        when(requestCode){
////            1000 ->
////                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
////                    Toast.makeText(context,"On Request Permissons called" ,Toast.LENGTH_LONG).show()
////                    PickImage()
////                }else{
////                    Toast.makeText(context,"Storage Permissons denied" ,Toast.LENGTH_LONG).show()
////                }
////
////        }
////    }
//
//    private val permReqLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            val granted = permissions.entries.all {
//                it.value == true
//            }
//            if (granted) {
//                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
//                    val result = CropImage.getActivityResult(data)
//                        image = result.uri
//                        requireView().cropImageView.setImageUriAsync(image)
//                        Toast.makeText(context,"On Activity Result Called" ,Toast.LENGTH_LONG).show()
//
//                }            }
//        }
//
//
////    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
////            result: ActivityResult ->
////        if (result.resultCode == Activity.RESULT_OK) {
////                    PickImage()
////        } else {
////                    Toast.makeText(context, "Storage Permissons denied", Toast.LENGTH_LONG).show()
////                }
////
////    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when(requestCode){
//            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
//                val result = CropImage.getActivityResult(data)
//                if(resultCode == Activity.RESULT_OK){
//                    image = result.uri
//                    requireView().cropImageView.setImageUriAsync(image)
//                    Toast.makeText(context,"On Activity Result Called" ,Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }

//    private fun PickImage() {
//        Toast.makeText(context,"PICK IMAGE CALLED",Toast.LENGTH_LONG).show()
//        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(requireContext(),this)
//    }
//}