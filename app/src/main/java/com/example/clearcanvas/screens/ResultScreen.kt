package com.example.clearcanvas.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clearcanvas.data.SkincareData
import com.example.clearcanvas.navigation.Screen



@Suppress("DEPRECATION")
@Composable
fun ResultScreen(navController: NavController) {
    val skinType by remember { mutableStateOf(SkincareData.skinTypes.random()) }
    val products by remember {
        mutableStateOf(SkincareData.productMap[skinType]?.shuffled()?.take(3)
            ?: listOf("General moisturizer", "Gentle cleanser", "SPF 30+ sunscreen"))
    }

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "result_animation")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    // Entry animation
    var isVisible by remember { mutableStateOf(false) }
    val entryAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "entry_alpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightBrown,
                        Color.White,
                        LightBrown.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .alpha(entryAlpha),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Success Icon with animation
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = AccentGold.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Analysis Complete",
                            tint = AccentGold,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Analysis Complete!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Your personalized skincare recommendations",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PrimaryBrown.copy(alpha = 0.7f)
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // Skin Type Result Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    SecondaryBrown.copy(alpha = 0.1f),
                                    PrimaryBrown.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Skin Type",
                                tint = PrimaryBrown,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Your Skin Type",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = PrimaryBrown.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Text(
                                    text = skinType,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBrown
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confidence indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Confidence Star",
                                    tint = if (index < 4) AccentGold else AccentGold.copy(alpha = 0.3f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "92% Confidence",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PrimaryBrown.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // Recommended Products Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Recommended Products",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    products.forEachIndexed { index, product ->
                        ProductRecommendationItem(
                            product = product,
                            index = index,
                            shimmerOffset = shimmerOffset
                        )
                        if (index < products.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary Button
                Button(
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBrown
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Back to Home",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }

                // Secondary Button
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Journal.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryBrown
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PrimaryBrown, SecondaryBrown)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Open Journal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProductRecommendationItem(
    product: String,
    index: Int,
    shimmerOffset: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = LightBrown.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product number indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                SecondaryBrown,
                                PrimaryBrown
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Product name
            Text(
                text = product,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = DarkBrown
                ),
                modifier = Modifier.weight(1f)
            )

            // Shimmer effect for premium feel
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AccentGold.copy(alpha = 0.3f),
                                AccentGold,
                                AccentGold.copy(alpha = 0.3f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .alpha(0.5f + shimmerOffset * 0.5f)
            )
        }
    }
}