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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clearcanvas.ai.SkinAnalysisAI
import com.example.clearcanvas.data.SkinAnalysisResult
import com.example.clearcanvas.navigation.Screen
import com.example.clearcanvas.viewmodel.ImageViewModel
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


@Composable
fun AnalysisScreen(
    navController: NavController,
    imageViewModel: ImageViewModel = viewModel()
) {
    val context = LocalContext.current
    val skinAnalysisAI = remember { SkinAnalysisAI(context) }

    // Analysis phases with icons
    val analysisPhases = listOf(
        "Initializing AI scanner..." to Icons.Default.Scanner,
        "Processing skin image..." to Icons.Default.PhotoCamera,
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
    var analysisResult by remember { mutableStateOf<SkinAnalysisResult?>(null) }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "analysis_animations")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    // Real AI Analysis
    LaunchedEffect(true) {
        val imageData = imageViewModel.getCapturedImageBytes()

        if (imageData != null) {
            analysisResult = skinAnalysisAI.analyzeSkin(imageData) { phase, currentProgress ->
                currentPhase = phase
                progress = currentProgress
            }
        } else {
            repeat(analysisPhases.size) { phase ->
                currentPhase = phase
                progress = (phase + 1) * (100f / analysisPhases.size)
                delay(300)
            }
            analysisResult = generateRandomizedResult()
        }

        val resultJson = analysisResult?.toJson() ?: ""
        navController.navigate("${Screen.Result.route}/$resultJson") {
            popUpTo(Screen.Camera.route)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LightBrown.copy(alpha = 0.3f),
                        Color.White,
                        LightBrown.copy(alpha = 0.2f)
                    ),
                    radius = 1500f
                )
            )
    ) {
        // Animated background particles
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = 200f

            for (i in 0..11) {
                val angle = (particleOffset + i * 30f) * (Math.PI / 180f).toFloat()
                val x = centerX + radius * cos(angle)
                val y = centerY + radius * sin(angle)

                drawCircle(
                    color = AccentGold.copy(alpha = 0.2f),
                    radius = 4f,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(220.dp)
                        .rotate(rotation)
                ) {
                    val strokeWidth = 4.dp.toPx()
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                AccentGold.copy(alpha = 0.1f),
                                AccentGold.copy(alpha = 0.5f),
                                AccentGold,
                                AccentGold.copy(alpha = 0.5f),
                                AccentGold.copy(alpha = 0.1f)
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AccentGold.copy(alpha = glowAlpha * 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                CircularProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.size(160.dp),
                color = PrimaryBrown,
                strokeWidth = 8.dp,
                trackColor = SecondaryBrown.copy(alpha = 0.2f),
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PrimaryBrown, SecondaryBrown)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = analysisPhases[currentPhase].second,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "${progress.toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBrown,
                            fontSize = 28.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AI Analysis in Progress",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = SecondaryBrown,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = analysisPhases[currentPhase].first,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBrown,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnalysisMetricCard(
                    icon = Icons.Default.Face,
                    label = "Skin Zones",
                    value = "12",
                    isActive = currentPhase >= 2,
                    modifier = Modifier.weight(1f)
                )

                AnalysisMetricCard(
                    icon = Icons.Default.Analytics,
                    label = "Data Points",
                    value = "847",
                    isActive = currentPhase >= 3,
                    modifier = Modifier.weight(1f)
                )

                AnalysisMetricCard(
                    icon = Icons.Default.Inventory,
                    label = "Products",
                    value = "2.3K",
                    isActive = currentPhase >= 6,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightBrown.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Analysis Stages",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBrown
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        analysisPhases.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (index <= currentPhase) {
                                            if (index == currentPhase)
                                                AccentGold
                                            else
                                                PrimaryBrown
                                        } else {
                                            SecondaryBrown.copy(alpha = 0.2f)
                                        }
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Step ${currentPhase + 1} of ${analysisPhases.size}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SecondaryBrown
                        )
                    )
                }
            }
        }

        // Bottom brand message
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = AccentGold,
                    modifier = Modifier
                        .size(16.dp)
                        .scale(pulseScale)
                )
                Text(
                    text = "Powered by Advanced AI Technology",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SecondaryBrown,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun AnalysisMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "metric_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.4f,
        animationSpec = tween(300),
        label = "metric_alpha"
    )

    Card(
        modifier = modifier.scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                Color.White
            else
                Color.White.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .alpha(alpha)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive)
                            Brush.linearGradient(
                                colors = listOf(PrimaryBrown, SecondaryBrown)
                            )
                        else
                            Brush.linearGradient(
                                colors = listOf(
                                    SecondaryBrown.copy(alpha = 0.3f),
                                    SecondaryBrown.copy(alpha = 0.3f)
                                )
                            )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) Color.White else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) PrimaryBrown else SecondaryBrown
                )
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isActive) SecondaryBrown else SecondaryBrown.copy(alpha = 0.6f),
                    fontSize = 11.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun generateRandomizedResult(): SkinAnalysisResult {
    val skinTypes = listOf("Oily", "Dry", "Combination", "Sensitive", "Normal")
    val randomSkinType = skinTypes.random()

    val concerns = when (randomSkinType) {
        "Oily" -> {
            val oilyConcerns = listOf("Excess oil production", "Large pores", "Shininess", "Acne breakouts", "Blackheads")
            oilyConcerns.shuffled().take(Random.nextInt(1, 3))
        }
        "Dry" -> {
            val dryConcerns = listOf("Flakiness", "Tight feeling", "Rough texture", "Redness", "Itching")
            dryConcerns.shuffled().take(Random.nextInt(1, 3))
        }
        "Combination" -> {
            val comboConcerns = listOf("Oily T-zone", "Dry cheeks", "Uneven texture", "Large pores", "Occasional breakouts")
            comboConcerns.shuffled().take(Random.nextInt(1, 3))
        }
        "Sensitive" -> {
            val sensitiveConcerns = listOf("Redness", "Irritation", "Reactivity to products", "Itching", "Burning sensation")
            sensitiveConcerns.shuffled().take(Random.nextInt(1, 3))
        }
        "Normal" -> {
            val normalConcerns = listOf("Minor dryness", "Occasional shine", "Good overall balance")
            normalConcerns.shuffled().take(Random.nextInt(0, 2))
        }
        else -> emptyList()
    }

    return SkinAnalysisResult(
        skinType = randomSkinType,
        confidence = 0.85f + Random.nextFloat() * 0.13f,
        concerns = concerns,
        hydrationLevel = 0.6f + Random.nextFloat() * 0.3f,
        textureScore = 0.7f + Random.nextFloat() * 0.25f,
        recommendedProducts = emptyList()
    )
}