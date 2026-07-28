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
import com.example.ui.components.LangGraphAgentsCard
import com.example.ui.components.QdrantVectorRagCard
import com.example.ui.components.YoloVisionOcrCard
import com.example.ui.theme.MetaBlue
import com.example.ui.viewmodel.SiteMindViewModel

@Composable
fun AiIntegrationScreen(
    viewModel: SiteMindViewModel,
    modifier: Modifier = Modifier
) {
    val aiState by viewModel.aiIntegrationState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("ai_integration_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Column {
                Text(
                    text = "PHASE 17 — ADVANCED AI INTEGRATION",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "LangGraph, YOLO, OCR & Qdrant",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Multimodal reasoning engine with real-time streaming, vector RAG & autonomous agents.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. LangGraph Multi-Agent Orchestrator
        item {
            LangGraphAgentsCard(
                agents = aiState.agents,
                onTriggerWorkflow = { viewModel.triggerLangGraphExecution() }
            )
        }

        // 2. YOLO Spatial Vision & OCR Spec Parser
        item {
            YoloVisionOcrCard(
                yoloDetections = aiState.yoloDetections,
                ocrSpec = aiState.ocrSpec
            )
        }

        // 3. Qdrant Vector RAG & Streaming Memory
        item {
            QdrantVectorRagCard(
                qdrantVectors = aiState.qdrantVectors,
                memoryLogs = aiState.memoryLogs,
                streamingTextBuffer = aiState.streamingTextBuffer
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
