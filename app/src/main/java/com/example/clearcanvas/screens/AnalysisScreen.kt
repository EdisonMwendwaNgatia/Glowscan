package com.example.clearcanvas.screens

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clearcanvas.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun AnalysisScreen(
    navController: NavController,
) {
    // Analysis phases
    val analysisPhases = listOf(
        "Initializing AI scanner..." to Icons.Default.Scanner,
        "Detecting skin zones..." to Icons.Default.Face,
        "Analyzing texture & tone..." to Icons.Default.Palette,
        "Identifying skin concerns..." to Icons.Default.Search,
        "Calculating hydration levels..." to Icons.Default.WaterDrop,
        "Matching product database..." to Icons.Default.Science,
        "Generating recommendations..." to Icons.Default.AutoAwesome,
        "Finalizing analysis..." to Icons.Default.Done
    )

    var currentPhase by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "analysis_animation")

    val scannerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanner_rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    // Progress animation
    LaunchedEffect(true) {
        repeat(analysisPhases.size) { phase ->
            currentPhase = phase

            // Animate progress for each phase
            val startProgress = phase * (100f / analysisPhases.size)
            val endProgress = (phase + 1) * (100f / analysisPhases.size)

            val animationDuration = if (phase == analysisPhases.size - 1) 500 else 300

            // Animate progress smoothly
            val animator = Animatable(startProgress)
            animator.animateTo(
                targetValue = endProgress,
                animationSpec = tween(animationDuration, easing = FastOutSlowInEasing)
            ) {
                progress = value
            }

            delay(if (phase == analysisPhases.size - 1) 200 else 100)
        }

        delay(500) // Final pause before navigation
        navController.navigate(Screen.Result.route) {
            popUpTo(Screen.Camera.route) // Clear back stack to camera
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightBrown,
                        Color.White,
                        LightBrown.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "AI Skin Analysis",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Our advanced AI is analyzing your skin",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = DarkBrown.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Main analysis visualization
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background glow
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(pulseScale * 0.8f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AccentGold.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Outer scanning rings
                repeat(3) { index ->
                    Canvas(
                        modifier = Modifier
                            .size(160.dp + (20.dp * index))

                            .rotate(scannerRotation + (index * 120f))
                    ) {
                        val strokeWidth = 3.dp.toPx()
                        val radius = size.minDimension / 2 - strokeWidth / 2

                        drawArc(
                            color = AccentGold.copy(alpha = 0.6f - (index * 0.2f)),
                            startAngle = 0f,
                            sweepAngle = 120f,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            ),
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(radius * 2, radius * 2)
                        )
                    }
                }

                // Central analysis circle
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = analysisPhases[currentPhase].second,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .alpha(shimmerAlpha),
                            tint = PrimaryBrown
                        )
                    }
                }

                // Progress indicator
                Canvas(
                    modifier = Modifier.size(180.dp)
                ) {
                    val strokeWidth = 8.dp.toPx()
                    val radius = size.minDimension / 2 - strokeWidth / 2

                    // Background circle
                    drawArc(
                        color = SecondaryBrown.copy(alpha = 0.3f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(radius * 2, radius * 2)
                    )

                    // Progress arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                AccentGold,
                                PrimaryBrown,
                                AccentGold
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = (progress / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        ),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(radius * 2, radius * 2)
                    )
                }

                // Scanning dots
                val density = LocalDensity.current

                repeat(8) { index ->
                    val angle = (index * 45f) + scannerRotation
                    val radius = with(density) { 100.dp.toPx() }  // ✅ Correct way

                    val x = radius * cos(Math.toRadians(angle.toDouble())).toFloat()
                    val y = radius * sin(Math.toRadians(angle.toDouble())).toFloat()

                    Canvas(
                        modifier = Modifier
                            .size(6.dp)
                            .offset(x = x.toFloat().dp, y = y.toFloat().dp)
                    ) {
                        drawCircle(
                            color = AccentGold.copy(alpha = 0.8f),
                            radius = with(density) { 3.dp.toPx() }  // ✅ Correct conversion
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(40.dp))

            // Progress percentage
            Text(
                text = "${progress.toInt()}%",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current phase description
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = analysisPhases[currentPhase].second,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = AccentGold
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = analysisPhases[currentPhase].first,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = DarkBrown
                        ),
                        modifier = Modifier.alpha(shimmerAlpha)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Analysis details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnalysisMetric(
                    icon = Icons.Default.Face,
                    label = "Skin Zones",
                    value = "12",
                    isActive = currentPhase >= 1
                )

                AnalysisMetric(
                    icon = Icons.Default.Palette,
                    label = "Data Points",
                    value = "847",
                    isActive = currentPhase >= 2
                )

                AnalysisMetric(
                    icon = Icons.Default.Science,
                    label = "Products",
                    value = "2.3K",
                    isActive = currentPhase >= 5
                )
            }
        }

        // Decorative elements
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentGold.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .align(Alignment.TopStart)
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SecondaryBrown.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun AnalysisMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isActive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(if (isActive) 1f else 0.4f)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isActive) AccentGold else DarkBrown.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isActive) PrimaryBrown else DarkBrown.copy(alpha = 0.5f)
            )
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isActive) DarkBrown.copy(alpha = 0.7f) else DarkBrown.copy(alpha = 0.4f)
            )
        )
    }
}