package com.example.clearcanvas.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.clearcanvas.R
import com.example.clearcanvas.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

// Brand Colors
val PrimaryBrown = Color(0xFF8B5A3C)
val SecondaryBrown = Color(0xFFD4A574)
val LightBrown = Color(0xFFF5E6D3)
val AccentGold = Color(0xFFD4AF37)

@Composable
fun SplashScreen(navController: NavController) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text_alpha"
    )

    LaunchedEffect(true) {
        delay(2000L) // 2 second delay
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LightBrown,
                        SecondaryBrown.copy(alpha = 0.8f),
                        PrimaryBrown
                    ),
                    radius = 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background pattern/texture effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            PrimaryBrown.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo with elegant container
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image), // Your app icon
                        contentDescription = "ClearCanvas Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name with elegant typography
            Text(
                text = "ClearCanvas",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = Color.White,
                modifier = Modifier.alpha(textAlpha),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Your Beauty, Redefined",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.8.sp
                ),
                color = AccentGold,
                modifier = Modifier.alpha(textAlpha * 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Custom Loading Indicator
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = AccentGold,
                    strokeWidth = 2.dp,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                // Inner pulsing dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .scale(logoScale * 0.5f)
                        .background(
                            color = AccentGold,
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading text
            Text(
                text = "Preparing your beauty journey...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp
                ),
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.alpha(textAlpha * 0.6f)
            )
        }

        // Bottom branding
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (index == 1) AccentGold else Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                    if (index < 2) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AI-Powered Skincare Recommendations",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.alpha(textAlpha * 0.5f)
            )
        }
    }
}