package com.example.clearcanvas.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.core.net.toUri

data class Dermatologist(
    val id: Int,
    val name: String,
    val specialty: String,
    val phoneNumber: String,
    val whatsappNumber: String,
    val experience: String,
    val rating: Float
)

@Composable
fun DermatologistsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dermatologists = listOf(
        Dermatologist(
            id = 1,
            name = "Dr. Sarah Johnson",
            specialty = "Acne & Scarring Specialist",
            phoneNumber = "+1234567890",
            whatsappNumber = "+1234567890",
            experience = "12 years",
            rating = 4.8f
        ),
        Dermatologist(
            id = 2,
            name = "Dr. Michael Chen",
            specialty = "Anti-Aging & Cosmetic Dermatology",
            phoneNumber = "+1234567891",
            whatsappNumber = "+1234567891",
            experience = "15 years",
            rating = 4.9f
        ),
        Dermatologist(
            id = 3,
            name = "Dr. Priya Sharma",
            specialty = "Skin Allergy & Eczema",
            phoneNumber = "+1234567892",
            whatsappNumber = "+1234567892",
            experience = "10 years",
            rating = 4.7f
        ),
        Dermatologist(
            id = 4,
            name = "Dr. James Wilson",
            specialty = "Laser & Aesthetic Dermatology",
            phoneNumber = "+1234567893",
            whatsappNumber = "+1234567893",
            experience = "8 years",
            rating = 4.6f
        ),
        Dermatologist(
            id = 5,
            name = "Dr. Maria Garcia",
            specialty = "Pediatric Dermatology",
            phoneNumber = "+1234567894",
            whatsappNumber = "+1234567894",
            experience = "14 years",
            rating = 4.9f
        )
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBrown)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "Dermatologists",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Consult Dermatologists",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBrown)
                .padding(innerPadding)
        ) {
            Text(
                text = "Certified Dermatologists",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(dermatologists.size) { index ->
                    DermatologistCard(
                        dermatologist = dermatologists[index],
                        onCallClick = { phoneNumber ->
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:$phoneNumber".toUri()
                            }
                            context.startActivity(intent)
                        },
                        onWhatsAppClick = { whatsappNumber ->
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://wa.me/$whatsappNumber".toUri()
                                setPackage("com.whatsapp")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DermatologistCard(
    dermatologist: Dermatologist,
    onCallClick: (String) -> Unit,
    onWhatsAppClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with name and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = dermatologist.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                    )
                    Text(
                        text = dermatologist.specialty,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PrimaryBrown,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Rating
                Card(
                    colors = CardDefaults.cardColors(containerColor = AccentGold.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⭐ ${dermatologist.rating}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Experience
            Text(
                text = "Experience: ${dermatologist.experience}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryBrown.copy(alpha = 0.8f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Call Button
                Button(
                    onClick = { onCallClick(dermatologist.phoneNumber) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBrown
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call")
                }

                // WhatsApp Button
                Button(
                    onClick = { onWhatsAppClick(dermatologist.whatsappNumber) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatsapp,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("WhatsApp")
                }
            }
        }
    }
}