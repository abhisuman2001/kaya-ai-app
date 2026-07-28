package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PillarAuditListCard
import com.example.ui.components.PlayStoreChecklistCard
import com.example.ui.components.ProductionReadinessCard
import com.example.ui.theme.MetaBlue
import com.example.ui.viewmodel.SiteMindViewModel

@Composable
fun ProductionReadinessScreen(
    viewModel: SiteMindViewModel,
    modifier: Modifier = Modifier
) {
    val prodState by viewModel.productionState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("production_readiness_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Column {
                Text(
                    text = "PHASE 18 — PRODUCTION READINESS",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Performance, Security & Play Store Audit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "60 FPS Compose rendering, SQLCipher offline storage, Fastlane CI/CD & Play Store compliance.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Readiness Gauge & Audit Runner Card
        item {
            ProductionReadinessCard(
                readinessScorePct = prodState.overallReadinessScorePct,
                cacheStatus = prodState.cacheStatus,
                isRunningAudit = prodState.isRunningAudit,
                auditLogOutput = prodState.auditLogOutput,
                onRunAudit = { viewModel.runProductionAuditSuite() },
                onToggleOfflineMode = { enabled -> viewModel.toggleOfflineMode(enabled) },
                onClearAuditLog = { viewModel.clearProductionAuditLog() }
            )
        }

        // 2. 6-Pillar Quality Audit Checklist
        item {
            PillarAuditListCard(
                auditChecks = prodState.auditChecks,
                selectedPillar = prodState.selectedPillar,
                onPillarSelected = { pillar -> viewModel.selectProductionPillar(pillar) }
            )
        }

        // 3. Play Store & App Store Deployment Checklist
        item {
            PlayStoreChecklistCard(
                storeChecklist = prodState.storeChecklist
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
