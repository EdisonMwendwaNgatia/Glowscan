package com.example.clearcanvas.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {
    private var capturedImage: Bitmap? = null
    private var capturedImageBytes: ByteArray? = null

    fun setCapturedImage(bitmap: Bitmap, bytes: ByteArray) {
        capturedImage = bitmap
        capturedImageBytes = bytes
    }

    fun getCapturedImageBytes(): ByteArray? {
        return capturedImageBytes
    }

    fun getCapturedImage(): Bitmap? {
        return capturedImage
    }

    fun clearImageData() {
        capturedImage = null
        capturedImageBytes = null
    }
}