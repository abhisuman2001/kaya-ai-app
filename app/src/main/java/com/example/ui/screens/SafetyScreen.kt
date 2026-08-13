package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.data.local.HazardEntity
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeMedium
import com.example.ui.theme.ShapeSmall
import com.example.ui.theme.ShapeXLarge
import com.example.ui.viewmodel.SiteMindViewModel

@Composable
fun SafetyScreen(
    viewModel: SiteMindViewModel,
    modifier: Modifier = Modifier
) {
    val activeHazards by viewModel.activeHazards.collectAsStateWithLifecycle()
    val allHazards by viewModel.allHazards.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showAddModal by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var locationInput by remember { mutableStateOf("Grid B-4 Level 3") }
    var categoryInput by remember { mutableStateOf("Fall Risk") }
    var descInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AUTOMATED SITE SAFETY & HAZARDS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Safety & Hazard Center",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (currentUser.role == UserRole.SUPERVISOR) {
                    Button(
                        onClick = { showAddModal = !showAddModal },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = ShapeMedium,
                        modifier = Modifier.testTag("log_hazard_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Report Hazard", fontSize = 11.sp)
                    }
                }
            }
        }

        // Safety Compliance Score Gauge Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, ShapeXLarge),
                shape = ShapeXLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(LocalKayaColors.current.status.success.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "96%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LocalKayaColors.current.status.success)
                            Text(text = "OSHA", fontSize = 9.sp, color = LocalKayaColors.current.status.success, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Site Safety Index", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = "Ray-Ban Meta glasses continuously auditing PPE, fall hazards, crane proximity, and scaffolding.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Add New Hazard Form Collapsible
        if (showAddModal) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeXLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "REPORT NEW SAFETY HAZARD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Hazard Title") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hazard_title_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = locationInput,
                            onValueChange = { locationInput = it },
                            label = { Text("Location / Zone") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text("Description & Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (titleInput.isNotBlank()) {
                                    viewModel.addHazard(
                                        title = titleInput,
                                        category = categoryInput,
                                        severity = "HIGH",
                                        location = locationInput,
                                        description = descInput.ifBlank { "Manually reported hazard from Kaya AI app." }
                                    )
                                    showAddModal = false
                                    titleInput = ""
                                    descInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = ShapeMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_hazard_button")
                        ) {
                            Text("Save Hazard Record", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active Hazards Header
        item {
            Text(
                text = "OPEN SAFETY HAZARD LOG (${activeHazards.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }

        items(allHazards) { hazard ->
            HazardDetailCard(
                hazard = hazard,
                onResolve = { viewModel.resolveHazard(hazard) },
                isSupervisor = currentUser.role == UserRole.SUPERVISOR
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun HazardDetailCard(
    hazard: HazardEntity,
    onResolve: () -> Unit,
    isSupervisor: Boolean
) {
    val isCritical = hazard.severity == "CRITICAL"
    val isResolved = hazard.isResolved

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, ShapeXLarge),
        shape = ShapeXLarge,
        colors = CardDefaults.cardColors(
            containerColor = if (isResolved) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = ShapeSmall,
                    color = when {
                        isResolved -> LocalKayaColors.current.status.success.copy(alpha = 0.15f)
                        isCritical -> LocalKayaColors.current.status.error.copy(alpha = 0.15f)
                        else -> LocalKayaColors.current.status.warning.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = if (isResolved) "RESOLVED" else "${hazard.severity} • ${hazard.category}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isResolved -> LocalKayaColors.current.status.success
                            isCritical -> LocalKayaColors.current.status.error
                            else -> LocalKayaColors.current.status.warning
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = hazard.location,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = hazard.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = hazard.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            if (!isResolved) {
                if (isSupervisor) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onResolve,
                            colors = ButtonDefaults.buttonColors(containerColor = LocalKayaColors.current.status.success),
                            shape = ShapeSmall,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Resolve", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = ShapeSmall,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Notify Team", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            } else if (hazard.actionTaken.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Resolution: ${hazard.actionTaken}",
                    fontSize = 11.sp,
                    color = LocalKayaColors.current.status.success,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
