package com.matthewparsons.hookline.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matthewparsons.hookline.domain.model.PatternStep
import com.matthewparsons.hookline.domain.repository.SavedPattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternDetailScreen(
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: PatternDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pattern") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    if (state is PatternDetailUiState.Loaded) {
                        TextButton(onClick = { showDelete = true }) { Text("Delete") }
                    }
                },
            )
        },
    ) { padding ->
        when (val s = state) {
            PatternDetailUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            PatternDetailUiState.NotFound -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { Text("Pattern not found.") }

            is PatternDetailUiState.Loaded -> PatternBody(
                saved = s.saved,
                modifier = Modifier.padding(padding),
            )
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Delete pattern?") },
            text = { Text("This pattern will be removed from your history.") },
            confirmButton = {
                TextButton(onClick = {
                    showDelete = false
                    viewModel.delete(onDeleted)
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun PatternBody(saved: SavedPattern, modifier: Modifier = Modifier) {
    val pattern = saved.pattern
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = pattern.input.shape.displayName,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Yarn ${pattern.input.yarn.number} (${pattern.input.yarn.displayName}) • " +
                "${pattern.input.hook.millimetres} mm hook • " +
                "${pattern.input.baseStitch.displayName}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider()

        Section(
            title = "Starting chain",
            headline = if (pattern.startingChain.count == 0) "Magic ring"
            else "${pattern.startingChain.count} chain${if (pattern.startingChain.count == 1) "" else "s"}",
            body = pattern.startingChain.description,
        )

        HorizontalDivider()

        Section(
            title = "Estimated yarn",
            headline = "${"%.1f".format(pattern.yarnEstimate.yards)} yards • " +
                "${"%.1f".format(pattern.yarnEstimate.metres)} m",
            body = "Includes ${(pattern.yarnEstimate.marginFraction * 100).toInt()}% margin for tails and weaving in. " +
                "Treat as an estimate — real gauge varies per crocheter.",
        )

        HorizontalDivider()

        Text("Instructions", style = MaterialTheme.typography.titleMedium)
        pattern.steps.forEach { StepCard(it) }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "${pattern.totalBaseStitches} ${pattern.input.baseStitch.abbreviation} total.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Section(title: String, headline: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(headline, style = MaterialTheme.typography.headlineMedium)
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StepCard(step: PatternStep) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = step.instruction,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp),
        )
    }
}
