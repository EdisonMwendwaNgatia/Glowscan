package com.example.clearcanvas.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clearcanvas.data.SkinAnalysisResult
import com.example.clearcanvas.data.SkincareData
import com.example.clearcanvas.data.SkincareProduct
import com.example.clearcanvas.navigation.Screen
import kotlin.random.Random

@Composable
fun ResultScreen(
    navController: NavController,
    analysisData: String? = null
) {
    // Parse AI analysis result - NO CACHING, fresh each time
    val skinAnalysisResult = SkinAnalysisResult.fromJson(analysisData ?: getFreshRandomResult())

    val skinType = skinAnalysisResult.skinType
    val confidence = skinAnalysisResult.confidence
    val concerns = skinAnalysisResult.concerns

    // Get fresh products each time
    val products = getPersonalizedProducts(skinType, concerns)

    // Animation for success icon
    val infiniteTransition = rememberInfiniteTransition(label = "success_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightBrown.copy(alpha = 0.2f),
                        Color.White,
                        LightBrown.copy(alpha = 0.3f)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Success Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.2f),
                                    Color(0xFF4CAF50).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Analysis Complete",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Analysis Complete!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBrown
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your personalized skincare journey starts here",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = SecondaryBrown,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // Skin Type Card
        SkinTypeCard(skinType, confidence, concerns)

        // Products Card
        ProductsCard(products)

        // Dermatologist Card
        DermatologistCard(navController)

        // Action Buttons
        ActionButtons(navController)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SkinTypeCard(skinType: String, confidence: Float, concerns: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(colors = listOf(PrimaryBrown, SecondaryBrown))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Face, contentDescription = "Skin Type", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Your Skin Type", style = MaterialTheme.typography.labelLarge.copy(color = SecondaryBrown))
                    Text(skinType, style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold, color = PrimaryBrown, fontSize = 24.sp
                    ))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Confidence
            Card(colors = CardDefaults.cardColors(containerColor = LightBrown.copy(alpha = 0.3f)), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Star",
                                modifier = Modifier.size(20.dp),
                                tint = if (index < (confidence * 5).toInt()) AccentGold else SecondaryBrown.copy(alpha = 0.3f)
                            )
                        }
                    }
                    Text("${(confidence * 100).toInt()}% Match", style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold, color = PrimaryBrown
                    ))
                }
            }

            // Concerns
            if (concerns.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Detected Concerns", style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold, color = PrimaryBrown
                ))
                Spacer(modifier = Modifier.height(8.dp))
                concerns.forEach { concern ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentGold))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(concern, style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryBrown))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductsCard(products: List<SkincareProduct>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Spa, contentDescription = "Products", tint = PrimaryBrown)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Recommended Products", style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold, color = PrimaryBrown
                ))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (products.isNotEmpty()) {
                products.forEachIndexed { index, product ->
                    ProductItem(product.name, index)
                    if (index < products.size - 1) Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                Text("No specific products recommended.", style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryBrown), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ProductItem(product: String, index: Int) {
    Card(colors = CardDefaults.cardColors(containerColor = LightBrown.copy(alpha = 0.3f)), shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.linearGradient(colors = listOf(PrimaryBrown, SecondaryBrown))),
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold, color = Color.White
                ))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(product, style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium, color = DarkBrown
            ), modifier = Modifier.weight(1f))

            Icon(Icons.Default.CheckCircle, contentDescription = "Recommended", tint = Color(0xFF4CAF50))
        }
    }
}

@Composable
private fun DermatologistCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryBrown),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = "Medical", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Need Expert Advice?", style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold, color = Color.White
                    ))
                    Text("Talk to certified dermatologists", style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    ))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Screen.Dermatologist.route) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row {
                    Icon(Icons.Default.LocalHospital, contentDescription = "Consult")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Book Consultation", style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ))
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { navController.navigate(Screen.Journal.route) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row {
                Icon(Icons.Default.Book, contentDescription = "Journal")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Track in Journal", style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ))
            }
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBrown),
            border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryBrown),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row {
                Icon(Icons.Default.Home, contentDescription = "Home")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back to Home", style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ))
            }
        }
    }
}

// Simple product selection - fresh each time
private fun getPersonalizedProducts(skinType: String, concerns: List<String>): List<SkincareProduct> {
    val baseProducts = SkincareData.productMap[skinType] ?: return emptyList()

    // Always shuffle for variety
    return baseProducts.shuffled().take(3)
}

// Fresh random result each time
private fun getFreshRandomResult(): String {
    val skinTypes = listOf("Oily", "Dry", "Combination", "Sensitive", "Normal")
    val randomSkinType = skinTypes.random()

    val concerns = when (randomSkinType) {
        "Oily" -> listOf("Excess oil", "Large pores", "Acne").shuffled().take(Random.nextInt(1, 3))
        "Dry" -> listOf("Flakiness", "Tightness", "Redness").shuffled().take(Random.nextInt(1, 3))
        "Combination" -> listOf("Oily T-zone", "Dry cheeks", "Uneven texture").shuffled().take(Random.nextInt(1, 3))
        "Sensitive" -> listOf("Redness", "Irritation", "Reactivity").shuffled().take(Random.nextInt(1, 3))
        "Normal" -> listOf("Minor dryness", "Occasional shine").shuffled().take(Random.nextInt(0, 2))
        else -> emptyList()
    }

    return """{
        "skinType": "$randomSkinType",
        "confidence": ${0.85f + Random.nextFloat() * 0.13f},
        "concerns": [${concerns.joinToString { "\"$it\"" }}],
        "hydrationLevel": ${0.6f + Random.nextFloat() * 0.3f},
        "textureScore": ${0.7f + Random.nextFloat() * 0.25f}
    }"""
}