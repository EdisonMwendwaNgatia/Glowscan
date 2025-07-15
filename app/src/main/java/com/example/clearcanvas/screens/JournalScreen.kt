package com.example.clearcanvas.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clearcanvas.data.JournalDatabaseHelper
import com.example.clearcanvas.viewmodel.JournalEntry
import kotlinx.coroutines.delay



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(context: Context) {
    val db = remember { JournalDatabaseHelper(context) }
    var productName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var helpful by remember { mutableStateOf(false) }

    var entries by remember { mutableStateOf(db.getAll()) }
    var editingEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var isFormExpanded by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    var isScrolling by remember { mutableStateOf(false) }

    // Track scrolling state
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            isScrolling = true
            // Auto-collapse form when scrolling down
            if (listState.firstVisibleItemScrollOffset > 0) {
                isFormExpanded = false
            }
        } else {
            delay(100) // Small delay to avoid flickering
            isScrolling = false
        }
    }

    // Animation for button color
    val buttonColor by animateColorAsState(
        targetValue = if (editingEntry == null) PrimaryBrown else AccentGold,
        animationSpec = tween(300),
        label = "button_color"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightBrown.copy(alpha = 0.3f),
                        Color.White,
                        LightBrown.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        // Header Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Journal",
                        tint = PrimaryBrown,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Beauty Journal",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBrown
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Track your skincare journey and product experiences",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SecondaryBrown,
                        fontSize = 14.sp
                    )
                )
            }
        }

        // Collapsible Form Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Always visible header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (editingEntry == null) Icons.Default.Add else Icons.Default.Edit,
                            contentDescription = if (editingEntry == null) "Add Entry" else "Edit Entry",
                            tint = if (editingEntry == null) PrimaryBrown else AccentGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (editingEntry == null) "New Entry" else "Edit Entry",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (editingEntry == null) PrimaryBrown else AccentGold
                            )
                        )
                    }

                    // Expand/Collapse button
                    IconButton(
                        onClick = { isFormExpanded = !isFormExpanded }
                    ) {
                        Icon(
                            imageVector = if (isFormExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isFormExpanded) "Collapse" else "Expand",
                            tint = PrimaryBrown
                        )
                    }
                }

                // Animated form content
                AnimatedVisibility(
                    visible = isFormExpanded,
                    enter = expandVertically(
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = shrinkVertically(
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = {
                                Text(
                                    "Product Used",
                                    color = SecondaryBrown
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBrown,
                                focusedLabelColor = PrimaryBrown,
                                cursorColor = PrimaryBrown
                            ),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = "Product",
                                    tint = SecondaryBrown
                                )
                            }
                        )

                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = {
                                Text(
                                    "Duration Used",
                                    color = SecondaryBrown
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBrown,
                                focusedLabelColor = PrimaryBrown,
                                cursorColor = PrimaryBrown
                            ),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Duration",
                                    tint = SecondaryBrown
                                )
                            }
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = LightBrown.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Checkbox(
                                    checked = helpful,
                                    onCheckedChange = { helpful = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PrimaryBrown,
                                        uncheckedColor = SecondaryBrown
                                    )
                                )
                                Column {
                                    Text(
                                        text = "Was it helpful?",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = DarkBrown
                                        )
                                    )
                                    Text(
                                        text = "Rate your experience with this product",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SecondaryBrown,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (editingEntry == null) {
                                    db.insert(JournalEntry(productName = productName, duration = duration, helpful = helpful))
                                } else {
                                    db.update(JournalEntry(id = editingEntry!!.id, productName, duration, helpful))
                                    editingEntry = null
                                }
                                productName = ""
                                duration = ""
                                helpful = false
                                entries = db.getAll()
                                isFormExpanded = false // Auto-collapse after saving
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (editingEntry == null) Icons.Default.Save else Icons.Default.Update,
                                    contentDescription = if (editingEntry == null) "Save" else "Update",
                                    tint = Color.White
                                )
                                Text(
                                    text = if (editingEntry == null) "Save Entry" else "Update Entry",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Quick add hint when collapsed
                if (!isFormExpanded && editingEntry == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to add a new entry",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SecondaryBrown.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Entries Section
        if (entries.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Entries",
                    tint = PrimaryBrown,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Your Entries (${entries.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                    )
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entry.productName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBrown
                                    )
                                )

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (entry.helpful)
                                            Color(0xFF4CAF50).copy(alpha = 0.1f)
                                        else
                                            Color(0xFFFF5722).copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (entry.helpful) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                                            contentDescription = if (entry.helpful) "Helpful" else "Not Helpful",
                                            tint = if (entry.helpful) Color(0xFF4CAF50) else Color(0xFFFF5722),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (entry.helpful) "Helpful" else "Not Helpful",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (entry.helpful) Color(0xFF4CAF50) else Color(0xFFFF5722),
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Duration",
                                    tint = SecondaryBrown,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Used for: ${entry.duration}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = SecondaryBrown
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        productName = entry.productName
                                        duration = entry.duration
                                        helpful = entry.helpful
                                        editingEntry = entry
                                        isFormExpanded = true // Auto-expand when editing
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = PrimaryBrown
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        PrimaryBrown
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit")
                                }

                                OutlinedButton(
                                    onClick = {
                                        db.delete(entry.id)
                                        entries = db.getAll()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFD32F2F)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Color(0xFFD32F2F)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }

                // Add some bottom padding
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // Changed from CenterVertically
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "No entries",
                    tint = SecondaryBrown,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No entries yet",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = SecondaryBrown,
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    text = "Start tracking your skincare journey!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SecondaryBrown.copy(alpha = 0.7f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}