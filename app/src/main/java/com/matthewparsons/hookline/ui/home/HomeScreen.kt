package com.matthewparsons.hookline.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matthewparsons.hookline.domain.repository.SavedPattern
import com.matthewparsons.hookline.domain.repository.isComplete
import com.matthewparsons.hookline.domain.repository.percentComplete
import java.text.DateFormat
import java.util.Date

private val CompleteGreen = Color(0xFF2E7D32)
private val CompleteGreenContainer = Color(0xFFE8F5E9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewPattern: () -> Unit,
    onPatternClick: (String) -> Unit,
    onSignOut: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val patterns by viewModel.patterns.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hookline") },
                actions = {
                    TextButton(onClick = onSignOut) { Text("Sign out") }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewPattern,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New pattern") },
            )
        },
    ) { padding ->
        if (patterns.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = patterns, key = { it.id }) { saved ->
                    PatternListItem(saved = saved, onClick = { onPatternClick(saved.id) })
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "No patterns yet",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tap the + button to generate your first crochet pattern.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PatternListItem(saved: SavedPattern, onClick: () -> Unit) {
    val pattern = saved.pattern
    val date = DateFormat.getDateInstance(DateFormat.MEDIUM)
        .format(Date(saved.createdAtEpochMs))
    val percent = saved.percentComplete
    val complete = saved.isComplete

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = pattern.input.shape.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                if (complete) CompletePill()
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = buildString {
                    append("${pattern.totalBaseStitches} ${pattern.input.baseStitch.abbreviation}")
                    append(" • ${"%.1f".format(pattern.yarnEstimate.yards)} yd")
                    append(" • $date")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            ProgressRow(percent = percent, complete = complete)
        }
    }
}

@Composable
private fun ProgressRow(percent: Float, complete: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier.weight(1f),
            color = if (complete) CompleteGreen
            else MaterialTheme.colorScheme.primary,
            trackColor = if (complete) CompleteGreenContainer
            else MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(Modifier.padding(end = 8.dp))
        Text(
            text = "${(percent * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            color = if (complete) CompleteGreen
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CompletePill() {
    Surface(
        shape = RoundedCornerShape(50),
        color = CompleteGreenContainer,
        contentColor = CompleteGreen,
    ) {
        Text(
            text = "Complete",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
