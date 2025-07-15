package com.example.clearcanvas.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

object SkinClassifier {

    // Your skin type labels (must match model's output order)
    private val labels = listOf("Oily", "Dry", "Combination", "Sensitive", "Normal")

    // Model input settings (depends on your trained model)
    private const val MODEL_NAME = "skin_classifier.tflite"
    private const val INPUT_IMAGE_SIZE = 224 // Assuming 224x224 input
    private const val NUM_CLASSES = 5 // Update if you have more/fewer classes

    fun classifySkin(context: Context, bitmap: Bitmap): String {
        val resized = createScaledBitmap(bitmap, INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, true)
        val byteBuffer = convertBitmapToByteBuffer(resized)

        val assetFileDescriptor = context.assets.openFd(MODEL_NAME)
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        val modelByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        val interpreter = Interpreter(modelByteBuffer)

        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, NUM_CLASSES), DataType.FLOAT32)
        interpreter.run(byteBuffer, outputBuffer.buffer.rewind())

        val scores = outputBuffer.floatArray
        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1
        return if (maxIndex != -1) labels[maxIndex] else "Unknown"
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_IMAGE_SIZE * INPUT_IMAGE_SIZE * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(INPUT_IMAGE_SIZE * INPUT_IMAGE_SIZE)
        bitmap.getPixels(pixels, 0, INPUT_IMAGE_SIZE, 0, 0, INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE)

        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255f
            val g = (pixel shr 8 and 0xFF) / 255f
            val b = (pixel and 0xFF) / 255f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        return byteBuffer
    }
}
