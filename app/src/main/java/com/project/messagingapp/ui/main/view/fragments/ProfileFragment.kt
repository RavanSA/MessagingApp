package com.project.messagingapp.ui.main.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.project.messagingapp.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.awesomedialog.*
import com.project.messagingapp.BuildConfig
import com.project.messagingapp.ui.main.viewmodel.ProfileViewModel
import com.project.messagingapp.databinding.FragmentProfileBinding
import com.project.messagingapp.utils.IImageUpload
import com.project.messagingapp.utils.OnClickInterface
import kotlinx.android.synthetic.main.activity_user_registration_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.pick_image_dialog.view.*
import java.io.File


class ProfileFragment : Fragment(),OnClickInterface,IImageUpload{

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dialog:AlertDialog

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        profileBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,
            container,false)

        profileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
            .create(ProfileViewModel::class.java)


        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { userModel ->
                profileBinding.userModel = userModel
                ProfileFirstName.text = userModel.name
                ProfilePhone.text =  userModel.number.toString()
                ProfileStatus.text =  userModel.status
        })


        profileBinding.clickHandler = this

        return profileBinding.root
    }


    fun updateStatusAlertDialog(){
         Toast.makeText(context,"DIALOG WORKED",Toast.LENGTH_LONG).show()

         val updateDialog = AlertDialog.Builder(context)
        val layout:View = LayoutInflater.from(context).inflate(R.layout.dialog_layout,
            null,false)
        updateDialog.setView(layout)

        layout.btnEdit.setOnClickListener {
            val status: String = layout.updateDialog.text.toString()
            if(!status.isEmpty()){
                profileViewModel.updateStatus(status)
                dialog.dismiss()
            }
        }
        dialog = updateDialog.create()
        dialog.show()
    }

    override fun onClick(view: View) {

        when(view.id){
           R.id.ProfileStatus -> {
                Toast.makeText(context,"ONCLICK WORKED",Toast.LENGTH_LONG).show()
                updateStatusAlertDialog()
            }

            R.id.ProfileFirstName  -> {
                Toast.makeText(context,"ONCLICK WORKED",Toast.LENGTH_LONG).show()
                updateNameAlertDialog()
            }

            R.id.ProfilePhone -> {
                Toast.makeText(context,"Phone number can not changable",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun updateNameAlertDialog() {
        val updateDialog = AlertDialog.Builder(context)
        val layout:View = LayoutInflater.from(context).inflate(R.layout.dialog_layout,
            null,false)
        updateDialog.setView(layout)

        layout.btnEdit.setOnClickListener {
            val name: String = layout.updateDialog.text.toString()
            if(!name.isEmpty()){
                profileViewModel.updateName(name)
                dialog.dismiss()
            }
        }
        dialog = updateDialog.create()
        dialog.show()
    }

    fun updateImageDialog(){
        Toast.makeText(context,"DIALOG WORKED",Toast.LENGTH_LONG).show()

        val updateDialog = AlertDialog.Builder(context)
        val layout:View = LayoutInflater.from(context).inflate(R.layout.pick_image_dialog,
            null,false)
        updateDialog.setView(layout)

        layout.imageFromCamera.setOnClickListener {
            pickImage().launch("image/*")
        }

        layout.imageFromStorage.setOnClickListener {
            pickImage().launch("image/*")
        }

        dialog = updateDialog.create()
        dialog.show()
    }

    override fun takePicture(): ActivityResultLauncher<Uri> {
        val root =
            File(activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID + File.separator)
        root.mkdirs()
        val fname = "img_" + System.currentTimeMillis() + ".jpg"
        val sdImageMainDirectory = File(root, fname)

       val image = FileProvider.getUriForFile(requireContext(), "com.project.messagingapp.provider",
            sdImageMainDirectory)
        Log.d("IMAGEINCAMERA", image.toString())

        val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                Log.d("IMAGEINLAUNCHER", image.toString())
                Glide.with(this).load(image).into(UpdateProfilePic)
            }
        }
//        takePicture.launch(image)
        return takePicture
    }

    override fun pickImage(): ActivityResultLauncher<String> {
        val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
            uri?.let { it ->
                Glide.with(this).load(it).into(UpdateProfilePic)
            }
        }
        return pickImages
    }

}
