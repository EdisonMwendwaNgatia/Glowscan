package com.example.clearcanvas.screens

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.BitmapFactory
import android.graphics.YuvImage
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clearcanvas.navigation.Screen
import com.example.clearcanvas.viewmodel.ImageViewModel
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    imageViewModel: ImageViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera state
    val previewView = remember { PreviewView(context) }
    var hasCamera by remember { mutableStateOf(false) }
    var cameraFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    var flashEnabled by remember { mutableStateOf(false) }

    // ImageCapture use case
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // Start camera
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()

                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing)
                    .build()

                // Set flash mode
                imageCapture.flashMode = if (flashEnabled) ImageCapture.FLASH_MODE_ON
                else ImageCapture.FLASH_MODE_OFF

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                hasCamera = true
            } catch (exc: Exception) {
                Log.e("CameraScreen", "Camera failed: ${exc.message}")
                Toast.makeText(context, "Camera failed to start", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Take photo function
    fun takePhoto() {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    try {
                        val bitmap = imageProxy.toBitmap()
                        val bytes = bitmapToByteArray(bitmap)

                        // Store in ViewModel for analysis
                        imageViewModel.setCapturedImage(bitmap, bytes)

                        Log.d("CameraScreen", "Image captured successfully, navigating to analysis")

                        // Navigate to analysis
                        navController.navigate(Screen.Analysis.route)
                    } catch (e: Exception) {
                        Log.e("CameraScreen", "Image processing failed: ${e.message}", e)
                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                    } finally {
                        imageProxy.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraScreen", "Capture failed: ${exception.message}", exception)
                    Toast.makeText(context, "Photo capture failed", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Start camera on composition
    LaunchedEffect(cameraFacing, flashEnabled) {
        startCamera()
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    "Skin Analysis Camera",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            actions = {
                // Flash toggle
                IconButton(
                    onClick = { flashEnabled = !flashEnabled }
                ) {
                    Icon(
                        if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (flashEnabled) AccentGold else Color.White
                    )
                }

                // Camera switch
                IconButton(
                    onClick = {
                        cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_FRONT)
                            CameraSelector.LENS_FACING_BACK
                        else
                            CameraSelector.LENS_FACING_FRONT
                    }
                ) {
                    Icon(
                        Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

        // Camera preview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (hasCamera) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )

                // Face guide
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .border(
                            width = 2.dp,
                            color = AccentGold,
                            shape = CircleShape
                        )
                )

                // Instructions
                Text(
                    text = "Position face within circle",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            } else {
                // Loading state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = AccentGold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Initializing camera...", color = Color.White)
                }
            }
        }

        // Capture button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = { takePhoto() },
                containerColor = AccentGold,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    Icons.Default.Camera,
                    contentDescription = "Capture",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// FIXED: Proper ImageProxy to Bitmap conversion
private fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // Copy Y plane
    yBuffer.get(nv21, 0, ySize)

    // Copy UV planes
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, this.width, this.height), 80, out)
    val imageBytes = out.toByteArray()

    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    // Handle rotation for front camera
    return if (this.imageInfo.rotationDegrees == 270) {
        val matrix = Matrix().apply {
            postRotate(90f)
            postScale(-1f, 1f) // Mirror for front camera
        }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}

// Convert bitmap to byte array for AI processing
private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return stream.toByteArray()
}