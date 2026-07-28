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
import com.example.ui.components.BackendStatusCard
import com.example.ui.components.FastApiEndpointsCard
import com.example.ui.components.SwaggerDocsCard
import com.example.ui.theme.MetaBlue
import com.example.ui.viewmodel.SiteMindViewModel

@Composable
fun BackendScreen(
    viewModel: SiteMindViewModel,
    modifier: Modifier = Modifier
) {
    val backendState by viewModel.backendConsoleState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("backend_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Column {
                Text(
                    text = "PHASE 16 — BACKEND ARCHITECTURE",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "FastAPI & Enterprise API Console",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "PostgreSQL DB, Redis cache, Docker orchestration, Alembic migrations & Swagger OpenAPI endpoints.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Backend Server & Docker Status Card
        item {
            BackendStatusCard(status = backendState.serverStatus)
        }

        // 2. Swagger & OpenAPI Explorer Card
        item {
            SwaggerDocsCard()
        }

        // 3. FastAPI Interactive Endpoints Tester
        item {
            FastApiEndpointsCard(
                endpoints = backendState.endpointsList,
                selectedCategory = backendState.selectedCategory,
                isTestingApi = backendState.isTestingApi,
                activeTestLog = backendState.activeTestLog,
                onCategorySelected = { cat -> viewModel.selectApiCategory(cat) },
                onTestEndpoint = { epId -> viewModel.testFastApiEndpoint(epId) },
                onClearLog = { viewModel.clearApiTestLog() }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
