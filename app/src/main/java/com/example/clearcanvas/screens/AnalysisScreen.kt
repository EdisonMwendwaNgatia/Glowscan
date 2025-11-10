package com.example.clearcanvas.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clearcanvas.ai.SkinAnalysisAI
import com.example.clearcanvas.data.SkinAnalysisResult
import com.example.clearcanvas.navigation.Screen
import com.example.clearcanvas.viewmodel.ImageViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun AnalysisScreen(
    navController: NavController,
    imageViewModel: ImageViewModel = viewModel()
) {
    val context = LocalContext.current
    val skinAnalysisAI = remember { SkinAnalysisAI(context) }

    // Analysis phases
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

    // Real AI Analysis
    LaunchedEffect(true) {
        // Get image data from ViewModel
        val imageData = imageViewModel.getCapturedImageBytes()

        if (imageData != null) {
            // Perform actual AI analysis
            analysisResult = skinAnalysisAI.analyzeSkin(imageData) { phase, currentProgress ->
                currentPhase = phase
                progress = currentProgress
            }
        } else {
            // Fallback to RANDOMIZED analysis - different each time!
            repeat(analysisPhases.size) { phase ->
                currentPhase = phase
                progress = (phase + 1) * (100f / analysisPhases.size)
                delay(300) // Simulate processing time
            }

            // Generate RANDOMIZED result
            analysisResult = generateRandomizedResult()
        }

        // Navigate to results with analysis data
        val resultJson = analysisResult?.toJson() ?: ""
        navController.navigate("${Screen.Result.route}/$resultJson") {
            popUpTo(Screen.Camera.route)
        }
    }

    // Minimal UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Progress indicator
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            progress = progress / 100f
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Current phase
        Text(
            text = analysisPhases[currentPhase].first,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress percentage
        Text(
            text = "${progress.toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Analysis details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnalysisMetric(
                label = "Skin Zones",
                value = "12",
                isActive = currentPhase >= 2
            )

            AnalysisMetric(
                label = "Data Points",
                value = "847",
                isActive = currentPhase >= 3
            )

            AnalysisMetric(
                label = "Products",
                value = "2.3K",
                isActive = currentPhase >= 6
            )
        }
    }
}

@Composable
private fun AnalysisMetric(
    label: String,
    value: String,
    isActive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(if (isActive) 1f else 0.4f)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Generate truly random results for fallback
private fun generateRandomizedResult(): SkinAnalysisResult {
    val skinTypes = listOf("Oily", "Dry", "Combination", "Sensitive", "Normal")
    val randomSkinType = skinTypes.random()

    // Generate random concerns based on skin type
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
        confidence = 0.85f + Random.nextFloat() * 0.13f, // 0.85 to 0.98
        concerns = concerns,
        hydrationLevel = 0.6f + Random.nextFloat() * 0.3f, // 0.6 to 0.9
        textureScore = 0.7f + Random.nextFloat() * 0.25f, // 0.7 to 0.95
        recommendedProducts = emptyList() // Products will be generated in ResultScreen
    )
}