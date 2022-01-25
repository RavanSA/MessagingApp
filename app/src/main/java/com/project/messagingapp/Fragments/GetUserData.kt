package com.project.messagingapp.Fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.project.messagingapp.Activities.MainChatScreen
import com.project.messagingapp.Constants.AppConstants
import com.project.messagingapp.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_get_user_data.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GetUserData.newInstance] factory method to
 * create an instance of this fragment.
 */
class GetUserData : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var image: Uri? = null
    private lateinit var username:String
    private lateinit var status:String
    private var DatabaseRef: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageRef: StorageReference? = null
    private var ImageUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_get_user_data, container, false)
        view.UserProfileSave.setOnClickListener {
            if(CheckUserData()){
                UploadData(username,status,image!!)
            }
        }
        view.imgPickImage.setOnClickListener {
            if(CheckStoragePermisson()){
                PickImage()
            } else {
                StorageRequestPermisson()
            }
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GetUserData.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                GetUserData().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    private fun CheckUserData():Boolean{
        username = requireView().edtProfileName.toString().trim()
        status = requireView().edtProfileStatus.toString().trim()

        if(username.isEmpty()){
            requireView().edtProfileName.error = "Username cannot be empty"
            return false
        } else if(status.isEmpty()){
            requireView().edtProfileStatus.error = "Status can not be empty"
            return false
        } else if(image == null){
            Toast.makeText(context, "Image required", Toast.LENGTH_SHORT).show()
            return false
        } else return true
    }

    private fun UploadData(username: String, status: String, image: Uri) = kotlin.run {
        storageRef!!.child(firebaseAuth!!.uid + AppConstants.Path).putFile(image)
            .addOnSuccessListener {
                val task = it.storage.downloadUrl
                task.addOnCompleteListener { uri ->
                    ImageUrl = uri.result.toString()
                    val map = mapOf<String, String>(
                        "UserName" to username,
                        "Status" to status,
                        "Image" to ImageUrl!!
                    )
                    DatabaseRef!!.child(firebaseAuth!!.uid!!).updateChildren(map)
                    startActivity(Intent(context, MainChatScreen::class.java))
                    requireActivity().finish()
                }
            }
    }

    private fun CheckStoragePermisson(): Boolean {
        return  ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    }
    private fun StorageRequestPermisson() =
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),1000)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000 ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    PickImage()
                }else{
                    Toast.makeText(context,"Storage Permissons denied" ,Toast.LENGTH_LONG).show()
                }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                var result = CropImage.getActivityResult(data)
                if(resultCode == Activity.RESULT_OK){
                    image = result.uri
                    requireView().cropImageView.setImageUriAsync(image)
                }
            }
        }
    }

    private fun PickImage() {
        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(requireContext(),this)
    }
}