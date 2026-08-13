package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.network.SupabaseMediaAssetDto
import com.example.ui.components.ErrorState
import com.example.ui.components.Eyebrow
import com.example.ui.components.FilterPillRow
import com.example.ui.components.HairlineCard
import com.example.ui.components.LoadingState
import com.example.ui.components.TintPill
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeSmall
import com.example.ui.viewmodel.SiteMindViewModel

/** Filter tabs across the gallery, mirroring v1's media page. */
private enum class MediaFilter(val label: String) {
    Photos("Photos"),
    Videos("Videos"),
    Analysed("Analysed")
}

/**
 * The capture gallery — v1's `media.tsx`, which had no v2 equivalent at all.
 *
 * Reads `media_assets` directly, the same table the dashboard counts for "Captures analysed".
 * Thumbnails are not rendered yet: `storage_path` points into a **private** bucket and needs a
 * signed URL, so each tile shows its type, AI status and capture time rather than a broken
 * image. Showing a placeholder photo here would misrepresent what has actually synced.
 */
@Composable
fun MediaScreen(
    viewModel: SiteMindViewModel,
    modifier: Modifier = Modifier
) {
    val media by viewModel.mediaAssets.collectAsStateWithLifecycle()
    val loading by viewModel.mediaLoading.collectAsStateWithLifecycle()
    val error by viewModel.mediaError.collectAsStateWithLifecycle()

    var filter by remember { mutableStateOf<MediaFilter?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchMediaAssets() }

    val visible = when (filter) {
        null -> media
        MediaFilter.Photos -> media.filter { it.type == "photo" }
        MediaFilter.Videos -> media.filter { it.type == "video" }
        MediaFilter.Analysed -> media.filter { it.ai_status == "complete" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .testTag("media_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Eyebrow(text = "Captures")
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Site media",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (media.isEmpty()) {
                "Photos and clips from your sessions appear here."
            } else {
                "${media.size} capture${if (media.size == 1) "" else "s"} from your sessions."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilterPillRow(
            items = MediaFilter.entries.toList(),
            selected = filter,
            onSelect = { filter = it },
            label = { it.label },
            allLabel = "All",
            testTagPrefix = "media_filter"
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading && media.isEmpty() -> LoadingState(rows = 3, rowHeight = 120.dp)

            error != null -> ErrorState(
                message = error ?: "Could not load captures.",
                onRetry = { viewModel.fetchMediaAssets() }
            )

            visible.isEmpty() -> MediaEmptyState(hasAnyMedia = media.isNotEmpty())

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(visible, key = { it.id ?: it.hashCode().toString() }) { asset ->
                    MediaTile(asset = asset, onClick = { })
                }
            }
        }
    }
}

@Composable
private fun MediaTile(asset: SupabaseMediaAssetDto, onClick: () -> Unit) {
    val kaya = LocalKayaColors.current
    val isVideo = asset.type == "video"

    HairlineCard(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeSmall,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        Column {
            // Stands in for the thumbnail until signed Storage URLs are wired up.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = asset.title ?: if (isVideo) "Clip" else "Photo",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (asset.ai_status) {
                        "complete" -> TintPill(text = "Analysed", tint = kaya.status.success)
                        "failed" -> TintPill(text = "Failed", tint = kaya.status.error)
                        "processing" -> TintPill(text = "Analysing", tint = kaya.accent)
                        else -> TintPill(text = "Pending", tint = kaya.mutedForeground)
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaEmptyState(hasAnyMedia: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = if (hasAnyMedia) "Nothing in this filter" else "No captures yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (hasAnyMedia) {
                "Try a different filter."
            } else {
                "Start a Live AI session and your captures will land here."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
