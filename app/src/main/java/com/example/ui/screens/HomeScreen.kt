package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GlassAiState
import com.example.ui.components.AiCapsuleState
import com.example.ui.components.DeviceStatusRow
import com.example.ui.components.FloatingAiCapsule
import com.example.ui.components.GlassesHero
import com.example.ui.components.GlassesHeroSize
import com.example.ui.components.HairlineCard
import com.example.ui.components.ProgressRing
import com.example.ui.components.QuickActionTile
import com.example.ui.components.SectionTitle
import com.example.ui.components.StatTile
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeCircle
import com.example.ui.theme.ShapeLarge
import com.example.ui.theme.ShapeMedium
import com.example.ui.theme.ShapeXLarge
import com.example.ui.viewmodel.SiteMindViewModel
import java.util.Calendar

/**
 * Home, rebuilt to the v1 composition: greeting header, glasses hero card, an open-issues
 * row, the active-project card with its progress ring, then quick actions.
 *
 * Page padding is 24dp (v1's `px-6`) — the previous 16dp is a large part of why the old
 * build felt cramped.
 */
@Composable
fun HomeScreen(
    viewModel: SiteMindViewModel,
    onNavigateToLiveAi: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onNavigateToBlueprints: () -> Unit,
    onNavigateToKnowledge: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToDevice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassState by viewModel.glassState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val projectInfo by viewModel.projectInfo.collectAsStateWithLifecycle()
    val shiftInfo by viewModel.shiftInfo.collectAsStateWithLifecycle()
    val activeHazards by viewModel.activeHazards.collectAsStateWithLifecycle()

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.detectAndFetchLocationWeather()
        }
    }

    LaunchedEffect(Unit) {
        locationLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val isConnected = glassState.connectionState != GlassAiState.OFFLINE

    Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item("greeting") {
            GreetingHeader(
                userName = currentUser.name,
                unreadCount = activeHazards.size
            )
        }

        item("glasses_hero") {
            GlassesHeroCard(
                deviceName = glassState.deviceName,
                firmware = glassState.firmwareVersion,
                isConnected = isConnected,
                battery = glassState.batteryPercent,
                storagePercent = if (glassState.storageTotalGb > 0f) {
                    (((glassState.storageTotalGb - glassState.storageFreeGb) /
                        glassState.storageTotalGb) * 100f).toInt().coerceIn(0, 100)
                } else 0,
                isSyncing = glassState.isLiveStreaming,
                onConnect = { viewModel.toggleGlassConnection() },
                onOpenDevice = onNavigateToDevice
            )
        }

        item("open_issues") {
            OpenIssuesRow(
                count = activeHazards.size,
                onClick = onNavigateToSafety
            )
        }

        item("active_project") {
            SectionTitle(text = "Active project")
        }

        item("project_card") {
            ProjectCard(
                code = projectInfo.currentStage,
                name = projectInfo.name,
                location = projectInfo.location,
                progressPercent = (projectInfo.progressPercent * 100).toInt(),
                crewCount = shiftInfo.activeWorkerCount,
                hazardCount = activeHazards.size,
                onClick = onNavigateToReports
            )
        }

        item("quick_actions_title") {
            SectionTitle(text = "Quick actions")
        }

        item("quick_actions") {
            QuickActionsGrid(
                onSafety = onNavigateToSafety,
                onBlueprints = onNavigateToBlueprints,
                onKnowledge = onNavigateToKnowledge,
                onReports = onNavigateToReports
            )
        }
    }

        // v1's floating AI capsule: tap → Live AI, long-press → voice session.
        FloatingAiCapsule(
            state = if (glassState.isLiveStreaming) AiCapsuleState.Listening else AiCapsuleState.Ready,
            onTap = onNavigateToLiveAi,
            onLongPress = {
                viewModel.toggleLiveStream()
                onNavigateToLiveAi()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
        )
    }
}

@Composable
private fun GreetingHeader(userName: String, unreadCount: Int) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface, ShapeCircle)
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), ShapeCircle)
                    .testTag("home_notifications_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = if (unreadCount > 0) {
                        "Notifications, $unreadCount unread"
                    } else "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(LocalKayaColors.current.status.error, ShapeCircle)
                        .border(BorderStroke(2.dp, MaterialTheme.colorScheme.background), ShapeCircle),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassesHeroCard(
    deviceName: String,
    firmware: String,
    isConnected: Boolean,
    battery: Int,
    storagePercent: Int,
    isSyncing: Boolean,
    onConnect: () -> Unit,
    onOpenDevice: () -> Unit
) {
    val kaya = LocalKayaColors.current

    HairlineCard(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeXLarge,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deviceName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (isConnected) kaya.status.success else kaya.mutedForeground,
                                    ShapeCircle
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isConnected) "Online" else "Disconnected",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "  ·  ",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isConnected) "FW $firmware" else "Not paired",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Accent-tinted pill — one of the few places blue is allowed to appear.
                Row(
                    modifier = Modifier
                        .background(kaya.accent.copy(alpha = 0.15f), ShapeCircle)
                        .clickable(onClick = onOpenDevice)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("home_view_device_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Media",
                        style = MaterialTheme.typography.labelLarge,
                        color = kaya.accent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = kaya.accent,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Small: this is a device-status card, not the app's hero. Kaya AI leads with
            // the site, not the eyewear.
            GlassesHero(
                size = GlassesHeroSize.Small,
                isConnected = isConnected
            )

            DeviceStatusRow(
                battery = battery,
                storagePercent = storagePercent,
                isSyncing = isSyncing,
                isConnected = isConnected
            )

            if (!isConnected) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), ShapeCircle)
                        .clickable(onClick = onConnect)
                        .testTag("home_connect_glasses_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Connect glasses",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun OpenIssuesRow(count: Int, onClick: () -> Unit) {
    HairlineCard(
        modifier = Modifier.fillMaxWidth().testTag("home_open_issues_row"),
        shape = ShapeMedium,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Open safety issues",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "On this site · tap to review",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, ShapeCircle)
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ProjectCard(
    code: String,
    name: String,
    location: String,
    progressPercent: Int,
    crewCount: Int,
    hazardCount: Int,
    onClick: () -> Unit
) {
    HairlineCard(
        modifier = Modifier.fillMaxWidth().testTag("home_project_card"),
        shape = ShapeLarge,
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                ProgressRing(percent = progressPercent)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(
                    value = progressPercent.toString() + "%",
                    label = "Complete",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    value = crewCount.toString(),
                    label = "Crew",
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    value = hazardCount.toString(),
                    label = "Hazards",
                    tone = if (hazardCount > 0) LocalKayaColors.current.status.warning else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    onSafety: () -> Unit,
    onBlueprints: () -> Unit,
    onKnowledge: () -> Unit,
    onReports: () -> Unit
) {
    data class Action(val icon: ImageVector, val label: String, val onClick: () -> Unit)

    val actions = listOf(
        Action(Icons.Default.Shield, "Safety", onSafety),
        Action(Icons.Default.Architecture, "Plans", onBlueprints),
        Action(Icons.Default.MenuBook, "SOPs", onKnowledge),
        Action(Icons.Default.Description, "Reports", onReports)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.forEach { action ->
            QuickActionTile(
                icon = action.icon,
                label = action.label,
                onClick = action.onClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
