package com.example.clearcanvas.screens

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clearcanvas.analyzer.FaceDetectionAnalyzer
import com.example.clearcanvas.navigation.Screen
import com.example.clearcanvas.viewmodel.ImageViewModel
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    imageViewModel: ImageViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    var hasCamera by remember { mutableStateOf(false) }
    var cameraFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    var flashEnabled by remember { mutableStateOf(false) }

    var isFaceDetected by remember { mutableStateOf(false) }
    var faceDetectionActive by remember { mutableStateOf(false) }
    var faceCount by remember { mutableIntStateOf(0) }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "camera_animations")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val scannerOffset by infiniteTransition.animateFloat(
        initialValue = -280f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_offset"
    )

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()

                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing)
                    .build()

                imageCapture.flashMode = if (flashEnabled) ImageCapture.FLASH_MODE_ON
                else ImageCapture.FLASH_MODE_OFF

                val faceAnalysisExecutor = Executors.newSingleThreadExecutor()
                imageAnalysis.setAnalyzer(
                    faceAnalysisExecutor,
                    FaceDetectionAnalyzer(
                        onFacesDetected = { faces ->
                            faceCount = faces.size
                            isFaceDetected = faces.isNotEmpty()
                            faceDetectionActive = true

                            if (faces.isNotEmpty()) {
                                Log.d("CameraScreen", "Face detected: ${faces.size} faces")
                            }
                        },
                        onError = { e ->
                            Log.e("CameraScreen", "Face detection error: ${e.message}")
                            faceDetectionActive = false
                        }
                    )
                )

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )

                hasCamera = true
            } catch (exc: Exception) {
                Log.e("CameraScreen", "Camera failed: ${exc.message}")
                Toast.makeText(context, "Camera failed to start", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }, executor)
    }

    fun takePhoto() {
        if (!isFaceDetected) {
            Toast.makeText(context, "Please position your face in the frame", Toast.LENGTH_SHORT).show()
            return
        }

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    try {
                        val bitmap = imageProxy.toBitmap()
                        val bytes = bitmapToByteArray(bitmap)

                        imageViewModel.setCapturedImage(bitmap, bytes)
                        Log.d("CameraScreen", "Image captured successfully, navigating to analysis")
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

    DisposableEffect(Unit) {
        onDispose {
            imageAnalysis.clearAnalyzer()
        }
    }

    LaunchedEffect(cameraFacing, flashEnabled) {
        startCamera()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        if (hasCamera) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Dark overlay for better UI visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar with gradient background
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.8f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // Title
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Face Scan",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "AI Skin Analysis",
                                color = AccentGold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Camera controls
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { flashEnabled = !flashEnabled },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (flashEnabled)
                                            AccentGold.copy(alpha = 0.3f)
                                        else
                                            Color.Black.copy(alpha = 0.5f)
                                    )
                            ) {
                                Icon(
                                    if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Flash",
                                    tint = if (flashEnabled) AccentGold else Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_FRONT)
                                        CameraSelector.LENS_FACING_BACK
                                    else
                                        CameraSelector.LENS_FACING_FRONT
                                    isFaceDetected = false
                                    faceCount = 0
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    Icons.Default.Cameraswitch,
                                    contentDescription = "Switch Camera",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Center content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (hasCamera) {
                    // Face detection guide with animated border
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer glow ring
                        Canvas(
                            modifier = Modifier.size(320.dp)
                        ) {
                            val strokeWidth = 4.dp.toPx()
                            val radius = size.minDimension / 2

                            // Draw dashed circle guide
                            drawCircle(
                                color = if (isFaceDetected) Color(0xFF4CAF50) else AccentGold,
                                radius = radius - strokeWidth / 2,
                                style = Stroke(
                                    width = strokeWidth,
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(20f, 20f),
                                        0f
                                    )
                                )
                            )

                            // Draw corner indicators
                            val cornerSize = 40f
                            val cornerOffset = radius * 0.7f

                            // Top-left corner
                            drawLine(
                                color = AccentGold,
                                start = Offset(center.x - cornerOffset, center.y - cornerOffset),
                                end = Offset(center.x - cornerOffset + cornerSize, center.y - cornerOffset),
                                strokeWidth = 6f
                            )
                            drawLine(
                                color = AccentGold,
                                start = Offset(center.x - cornerOffset, center.y - cornerOffset),
                                end = Offset(center.x - cornerOffset, center.y - cornerOffset + cornerSize),
                                strokeWidth = 6f
                            )

                            // Top-right corner
                            drawLine(
                                color = AccentGold,
                                start = Offset(center.x + cornerOffset, center.y - cornerOffset),
                                end = Offset(center.x + cornerOffset - cornerSize, center.y - cornerOffset),
                                strokeWidth = 6f
                            )
                            drawLine(
                                color = AccentGold,
                                start = Offset(center.x + cornerOffset, center.y - cornerOffset),
                                end = Offset(center.x + cornerOffset, center.y - cornerOffset + cornerSize),
                                strokeWidth = 6f
                            )

                            // Bottom-left corner
                            drawLine(
                                color = AccentGold,
                                start = Offset(center.x - cornerOffset, center.y + cornerOffset),
                                end = Offset(center.x - cornerOffset + cornerSize, center.y + cornerOffset),
                                strokeWidth = 6f
                            )
                            drawLine(
                                color = AccentGold,
                                start = Offset(center.x - cornerOffset, center.y + cornerOffset),
                                end = Offset(center.x - cornerOffset, center.y + cornerOffset - cornerSize),
                                strokeWidth = 6f
                            )

                            // Bottom-right corner
                            drawLine(
                                color = AccentGold,
                                start = Offset(center.x + cornerOffset, center.y + cornerOffset),
                                end = Offset(center.x + cornerOffset - cornerSize, center.y + cornerOffset),
                                strokeWidth = 6f
                            )
                            drawLine(
                                color = AccentGold,
                                start = Offset(center.x + cornerOffset, center.y + cornerOffset),
                                end = Offset(center.x + cornerOffset, center.y + cornerOffset - cornerSize),
                                strokeWidth = 6f
                            )

                            // Animated scanning line
                            if (!isFaceDetected && faceDetectionActive) {
                                drawLine(
                                    color = AccentGold.copy(alpha = 0.6f),
                                    start = Offset(center.x - radius, center.y + scannerOffset),
                                    end = Offset(center.x + radius, center.y + scannerOffset),
                                    strokeWidth = 3f
                                )
                            }
                        }

                        // Center icon
                        if (isFaceDetected) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Face Detected",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Face Guide",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }

                    // Status card at top
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 100.dp)
                            .padding(horizontal = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            !faceDetectionActive -> Color.Gray
                                            isFaceDetected -> Color(0xFF4CAF50)
                                            else -> Color(0xFFFF9800)
                                        }
                                    )
                                    .then(
                                        if (isFaceDetected) Modifier.scale(pulseScale)
                                        else Modifier
                                    )
                            )

                            Column {
                                Text(
                                    text = when {
                                        !faceDetectionActive -> "Initializing..."
                                        isFaceDetected -> "Face Detected!"
                                        else -> "Position Your Face"
                                    },
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                if (isFaceDetected) {
                                    Text(
                                        text = "Ready to capture",
                                        color = AccentGold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else if (faceCount > 1) {
                                    Text(
                                        text = "Multiple faces detected",
                                        color = Color(0xFFFF9800),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Instructions card at bottom
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 180.dp)
                            .padding(horizontal = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Tips for Best Results",
                                color = AccentGold,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Center your face in the circle\n• Use good lighting\n• Remove glasses if possible",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Loading state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = AccentGold,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(60.dp)
                        )
                        Text(
                            "Initializing camera...",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Please wait",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Bottom capture button section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Face count indicator
                    if (isFaceDetected && faceCount > 0) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Face count",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$faceCount face${if (faceCount > 1) "s" else ""} detected",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    // Capture button
                    FloatingActionButton(
                        onClick = {
                            if (isFaceDetected) {
                                takePhoto()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please position your face in the frame first",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        containerColor = if (isFaceDetected) AccentGold else Color.Gray,
                        modifier = Modifier
                            .size(80.dp)
                            .then(
                                if (isFaceDetected) Modifier.scale(pulseScale)
                                else Modifier
                            )
                    ) {
                        Icon(
                            Icons.Default.Camera,
                            contentDescription = "Capture",
                            modifier = Modifier.size(36.dp),
                            tint = Color.White
                        )
                    }

                    Text(
                        text = if (isFaceDetected) "Tap to Capture" else "Waiting for face...",
                        color = if (isFaceDetected) AccentGold else Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return stream.toByteArray()
}