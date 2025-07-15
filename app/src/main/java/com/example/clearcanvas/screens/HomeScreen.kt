package com.example.clearcanvas.screens

import android.Manifest
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clearcanvas.navigation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

// Data classes for features
data class FeatureCard(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

data class SkincareTip(
    val title: String,
    val description: String,
    val icon: ImageVector
)

data class RecentScan(
    val date: String,
    val skinType: String,
    val recommendations: Int
)

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val userEmail = user?.email ?: "User"
    val userName = userEmail.split("@")[0].replaceFirstChar { it.uppercase() }
    val context = LocalContext.current

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Handle camera click with permission check
    fun handleCameraClick() {
        when {
            cameraPermissionState.status.isGranted -> {
                navController.navigate(Screen.Camera.route)
            }
            cameraPermissionState.status.shouldShowRationale -> {
                showPermissionDialog = true
            }
            else -> {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "home_animation")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )

    // Sample data
    val featureCards = listOf(
        FeatureCard(
            "AI Face Analysis",
            "Get personalized skincare recommendations",
            Icons.Default.Face,
            AccentGold,
            Screen.Camera.route
        ),
        FeatureCard(
            "Skin Progress",
            "Track your skincare journey",
            Icons.Default.Timeline,
            SecondaryBrown,
            "progress"
        ),
        FeatureCard(
            "Product Library",
            "Discover new skincare products",
            Icons.Default.LocalMall,
            PrimaryBrown,
            "products"
        ),
        FeatureCard(
            "My Profile",
            "View and edit your profile",
            Icons.Default.Person,
            DarkBrown,
            "profile"
        )
    )

    val skincareTips = listOf(
        SkincareTip(
            "Morning Routine",
            "Start with cleanser, then vitamin C serum",
            Icons.Default.WbSunny
        ),
        SkincareTip(
            "Hydration Key",
            "Drink water and use a good moisturizer",
            Icons.Default.Opacity
        ),
        SkincareTip(
            "Sun Protection",
            "Always apply SPF 30+ sunscreen daily",
            Icons.Default.Shield
        )
    )

    val recentScans = listOf(
        RecentScan("Today", "Combination", 5),
        RecentScan("3 days ago", "Oily T-Zone", 3),
        RecentScan("1 week ago", "Dry", 4)
    )

    val currentTime = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
    val greeting = when (currentTime) {
        in 5..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    // Permission rationale dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Camera Permission Required") },
            text = { Text("ClearCanvas needs camera access to analyze your skin. Please grant permission in settings.") },
            confirmButton = {
                Button(onClick = {
                    showPermissionDialog = false
                    cameraPermissionState.launchPermissionRequest()
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightBrown,
                        SecondaryBrown.copy(alpha = 0.3f),
                        Color.White
                    )
                )
            )
    ) {
        // Floating background elements
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 250.dp, y = 100.dp)
                .rotate(rotationAngle)
                .background(
                    color = AccentGold.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = (-50).dp, y = 400.dp)
                .rotate(-rotationAngle)
                .background(
                    color = PrimaryBrown.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PrimaryBrown.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = DarkBrown
                        )
                    )
                }

                // Profile Avatar
                Card(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { navController.navigate("profile") },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = AccentGold.copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = PrimaryBrown,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Action Card - Updated to use handleCameraClick
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = floatingOffset.dp)
                    .clickable { handleCameraClick() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryBrown
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    PrimaryBrown,
                                    DarkBrown
                                )
                            )
                        )
                        .padding(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Ready for Your",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "Skin Analysis?",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Get AI-powered skincare recommendations",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = AccentGold,
                                    fontSize = 14.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .scale(1f + shimmerAlpha * 0.1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            // ... (rest of your existing HomeScreen content remains the same)
            Spacer(modifier = Modifier.height(32.dp))

            // Feature Cards Section
            Text(
                text = "Explore Features",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(featureCards) { feature ->
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .height(120.dp)
                            .clickable { navController.navigate(feature.route) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = feature.color.copy(alpha = 0.1f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                feature.icon,
                                contentDescription = feature.title,
                                tint = feature.color,
                                modifier = Modifier.size(28.dp)
                            )

                            Column {
                                Text(
                                    text = feature.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBrown,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = feature.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PrimaryBrown.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // ... (rest of your existing sections remain unchanged)
            Spacer(modifier = Modifier.height(32.dp))

            // Recent Scans Section
            Text(
                text = "Recent Scans",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    recentScans.forEach { scan ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = AccentGold.copy(alpha = 0.2f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Face,
                                        contentDescription = "Scan",
                                        tint = AccentGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = scan.skinType,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DarkBrown
                                        )
                                    )
                                    Text(
                                        text = scan.date,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = PrimaryBrown.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "${scan.recommendations} tips",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = AccentGold,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        if (scan != recentScans.last()) {
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = LightBrown
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Skincare Tips Section
            Text(
                text = "Daily Skincare Tips",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            skincareTips.forEach { tip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightBrown.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = AccentGold.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                tip.icon,
                                contentDescription = tip.title,
                                tint = AccentGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = tip.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBrown
                                )
                            )
                            Text(
                                text = tip.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = PrimaryBrown.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}