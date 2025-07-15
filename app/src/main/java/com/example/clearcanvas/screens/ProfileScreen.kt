package com.example.clearcanvas.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clearcanvas.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val email = user?.email ?: "Unknown"

    var skinType by remember { mutableStateOf("") }
    var products by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(uid) {
        uid?.let {
            val ref = FirebaseDatabase.getInstance().getReference("users").child(uid)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    skinType = snapshot.child("skinType").getValue(String::class.java) ?: "N/A"
                    val productList = snapshot.child("recommendedProducts")
                        .children.mapNotNull { it.getValue(String::class.java) }
                    products = productList
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error loading profile", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with profile avatar
            ProfileHeader()

            Spacer(modifier = Modifier.height(32.dp))

            // Profile info cards
            ProfileInfoCard(
                icon = Icons.Default.Email,
                title = "Email",
                content = email,
                backgroundColor = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoCard(
                icon = Icons.Default.Face,
                title = "Skin Type",
                content = if (skinType.isNotEmpty()) skinType else "Not analyzed yet",
                backgroundColor = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recommended products section
            if (products.isNotEmpty()) {
                RecommendedProductsSection(products)
            } else {
                EmptyProductsCard()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout button
            LogoutButton {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentGold, PrimaryBrown)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )
        )

        Text(
            text = "Your beauty journey continues",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PrimaryBrown
            )
        )
    }
}

@Composable
fun ProfileInfoCard(
    icon: ImageVector,
    title: String,
    content: String,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        SecondaryBrown.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = PrimaryBrown,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PrimaryBrown,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = DarkBrown,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun RecommendedProductsSection(products: List<String>) {
    Column {
        Text(
            text = "✨ Recommended Products",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        products.forEach { product ->
            ProductItem(product)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProductItem(product: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(AccentGold, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = product,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DarkBrown,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.Star,
                contentDescription = "Recommended",
                tint = AccentGold,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun EmptyProductsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryBrown.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Face,
                contentDescription = "No analysis",
                tint = SecondaryBrown,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No skin analysis yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown
                )
            )

            Text(
                text = "Scan your face to get personalized product recommendations",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryBrown
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Red
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp
        )
    ) {
        Icon(
            Icons.Default.AutoDelete,
            contentDescription = "Logout",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Logout",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}