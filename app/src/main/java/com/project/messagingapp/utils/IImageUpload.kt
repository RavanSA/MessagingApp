package com.project.messagingapp.utils

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher


interface IImageUpload {
    fun takePicture(): ActivityResultLauncher<Uri>
    fun pickImage(): ActivityResultLauncher<String>
}