package com.matthewparsons.hookline.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matthewparsons.hookline.domain.model.PatternStep
import com.matthewparsons.hookline.domain.repository.SavedPattern
import com.matthewparsons.hookline.domain.repository.isComplete
import com.matthewparsons.hookline.domain.repository.percentComplete
import com.matthewparsons.hookline.domain.repository.remainingStitchCount

private val CompleteGreen = Color(0xFF2E7D32)
private val CompleteGreenContainer = Color(0xFFE8F5E9)

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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                PatternDetailUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                PatternDetailUiState.NotFound -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { Text("Pattern not found.") }

                is PatternDetailUiState.Loaded -> {
                    PatternBody(
                        saved = s.saved,
                        onToggleStep = viewModel::toggleStep,
                    )
                    FloatingProgress(
                        saved = s.saved,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                    )
                }
            }
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
private fun PatternBody(
    saved: SavedPattern,
    onToggleStep: (Int) -> Unit,
) {
    val pattern = saved.pattern
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            // Bottom padding leaves room behind the floating progress widget so the
            // last step isn't permanently hidden.
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
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
        Text(
            text = "Tap a step to mark it complete. Tap again to undo.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        pattern.steps.forEachIndexed { index, step ->
            StepCard(
                step = step,
                isCompleted = index in saved.completedStepIndices,
                onToggle = { onToggleStep(index) },
            )
        }
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
private fun StepCard(
    step: PatternStep,
    isCompleted: Boolean,
    onToggle: () -> Unit,
) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp),
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() },
            )
            Spacer(Modifier.padding(end = 4.dp))
            Text(
                text = step.instruction,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun FloatingProgress(
    saved: SavedPattern,
    modifier: Modifier = Modifier,
) {
    val complete = saved.isComplete
    val percent = (saved.percentComplete * 100).toInt()
    val remaining = saved.remainingStitchCount

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (complete) CompleteGreenContainer
        else MaterialTheme.colorScheme.secondaryContainer,
        contentColor = if (complete) CompleteGreen
        else MaterialTheme.colorScheme.onSecondaryContainer,
        tonalElevation = 6.dp,
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.End,
        ) {
            if (complete) {
                Text(
                    text = "Complete ✓",
                    style = MaterialTheme.typography.titleMedium,
                )
            } else {
                Text(
                    text = "$remaining sts left",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "$percent% complete",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
