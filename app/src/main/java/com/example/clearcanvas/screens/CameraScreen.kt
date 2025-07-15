package com.example.clearcanvas.screens

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clearcanvas.navigation.Screen
import com.example.clearcanvas.viewmodel.ImageViewModel
import java.util.concurrent.Executors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    imageViewModel: ImageViewModel = viewModel()
) {
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera state variables
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var hasCamera by remember { mutableStateOf(false) }
    var cameraFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    var flashEnabled by remember { mutableStateOf(false) }

    // Animation for capture button
    val infiniteTransition = rememberInfiniteTransition(label = "camera_animation")
    val captureButtonPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "capture_pulse"
    )

    // ImageCapture use case - defined here to be accessible in both startCamera and takePhoto
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(
                if (flashEnabled) ImageCapture.FLASH_MODE_ON
                else ImageCapture.FLASH_MODE_OFF
            )
            .build()
    }

    // Define startCamera function
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Build preview use case
                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                // Select camera based on facing direction
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing)
                    .build()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                hasCamera = true
            } catch(exc: Exception) {
                Log.e("CameraScreen", "Camera initialization failed", exc)
                Toast.makeText(context, "Camera failed to start", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Define takePhoto function
    fun takePhoto() {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        val bitmap = image.toBitmap()
                        capturedImage = bitmap
                        imageViewModel.capturedImage = bitmap
                    } finally {
                        image.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraScreen", "Photo capture failed: ${exception.message}", exception)
                    Toast.makeText(context, "Photo capture failed", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Start camera when screen loads or cameraFacing changes
    LaunchedEffect(cameraFacing, flashEnabled) {
        startCamera()
    }

    // Clean up when screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Skin Analysis",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBrown
                        )
                    )
                },
                navigationIcon = {
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = LightBrown
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.KeyboardDoubleArrowLeft,
                                contentDescription = "Back",
                                tint = PrimaryBrown
                            )
                        }
                    }
                },
                actions = {
                    // Flash toggle
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(2.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (flashEnabled) AccentGold else LightBrown
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        IconButton(
                            onClick = { flashEnabled = !flashEnabled },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = if (flashEnabled) "Flash On" else "Flash Off",
                                tint = if (flashEnabled) Color.White else PrimaryBrown
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Camera switch
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(2.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = LightBrown
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_FRONT)
                                    CameraSelector.LENS_FACING_BACK
                                else
                                    CameraSelector.LENS_FACING_FRONT
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Cameraswitch,
                                contentDescription = "Switch Camera",
                                tint = PrimaryBrown
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (capturedImage != null) {
                // Show captured image with options
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Image preview with elegant frame
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Image(
                                bitmap = capturedImage!!.asImageBitmap(),
                                contentDescription = "Captured image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )

                            // Overlay gradient for text readability
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                                    )
                            )

                            Text(
                                text = "Perfect! Ready for analysis",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                ),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                            )
                        }
                    }

                    // Action buttons with premium styling
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Retake button
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = LightBrown
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    capturedImage = null
                                    imageViewModel.capturedImage = null
                                },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = PrimaryBrown,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Retake",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryBrown
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Analyze button
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryBrown
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    navController.navigate(Screen.Analysis.route)
                                },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Analyze",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // Show camera preview
                if (hasCamera) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Camera preview with rounded corners
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            AndroidView(
                                factory = { previewView },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                            )
                        }

                        // Face guide overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(280.dp)
                                    .border(
                                        width = 3.dp,
                                        color = AccentGold,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                            )
                        }

                        // Instructions
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.7f)
                            )
                        ) {
                            Text(
                                text = "Position your face within the circle",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Capture button with premium design
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 40.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Card(
                                modifier = Modifier
                                    .size(80.dp)
                                    .scale(captureButtonPulse),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = AccentGold
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                            ) {
                                FloatingActionButton(
                                    onClick = { takePhoto() },
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    elevation = FloatingActionButtonDefaults.elevation(
                                        defaultElevation = 0.dp
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Camera,
                                        contentDescription = "Capture photo",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Loading state with branded design
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier.size(120.dp),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = LightBrown
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = PrimaryBrown,
                                        strokeWidth = 3.dp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Initializing camera...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryBrown
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}