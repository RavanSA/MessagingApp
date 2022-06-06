package com.project.messagingapp.utils


import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
class AgeDetection {

    private val inputImageSize = 200


    private val inputImageProcessor =
        ImageProcessor.Builder()
            .add(ResizeOp(inputImageSize, inputImageSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()


    private val p = 116

    var inferenceTime : Long = 0

    var interpreter : Interpreter? = null


    suspend fun predictAge(image: Bitmap) = withContext( Dispatchers.Main ) {
        val start = System.currentTimeMillis()
        val tensorInputImage = TensorImage.fromBitmap(image)
        val ageOutputArray = Array(1){ FloatArray(1) }
        val processedImageBuffer = inputImageProcessor.process(tensorInputImage).buffer
        interpreter?.run(
            processedImageBuffer,
            ageOutputArray
        )
        inferenceTime = System.currentTimeMillis() - start
        return@withContext ageOutputArray[0][0] * p
    }
}
