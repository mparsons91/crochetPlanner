package com.matthewparsons.hookline.ui.newpattern

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matthewparsons.hookline.domain.model.LengthUnit
import com.matthewparsons.hookline.domain.model.YarnWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPatternScreen(
    onPatternSaved: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: NewPatternViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.savedPatternId) {
        state.savedPatternId?.let { id ->
            onPatternSaved(id)
            viewModel.consumeNavigationEvent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New pattern") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            YarnWeightField(state.yarn, viewModel::setYarn)
            HookSizeField(state.hookSizeMm, state.errors["hook"], viewModel::setHookSize)
            ShapePicker(state.shape, viewModel::setShape)
            UnitToggle(state.unit, viewModel::setUnit)
            DimensionFields(state, viewModel)

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = viewModel::submit,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isSubmitting) "Generating…" else "Generate pattern")
            }
            state.submitError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YarnWeightField(selected: YarnWeight, onSelect: (YarnWeight) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = "${selected.number} – ${selected.displayName}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Yarn weight") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            YarnWeight.entries.forEach { yw ->
                DropdownMenuItem(
                    text = { Text("${yw.number} – ${yw.displayName}") },
                    onClick = {
                        onSelect(yw)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun HookSizeField(value: String, error: String?, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Hook size (mm)") },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ShapePicker(selected: ShapeKind, onSelect: (ShapeKind) -> Unit) {
    Column {
        Text("Shape", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        ShapeKind.entries.forEach { shape ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                RadioButton(
                    selected = selected == shape,
                    onClick = { onSelect(shape) },
                )
                Text(shape.displayName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitToggle(unit: LengthUnit, onSelect: (LengthUnit) -> Unit) {
    Column {
        Text("Units", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Row {
            FilterChip(
                selected = unit == LengthUnit.INCHES,
                onClick = { onSelect(LengthUnit.INCHES) },
                label = { Text("Inches") },
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = unit == LengthUnit.CENTIMETRES,
                onClick = { onSelect(LengthUnit.CENTIMETRES) },
                label = { Text("Centimetres") },
            )
        }
    }
}

@Composable
private fun DimensionFields(state: NewPatternUiState, viewModel: NewPatternViewModel) {
    val unitLabel = when (state.unit) {
        LengthUnit.INCHES -> "in"
        LengthUnit.CENTIMETRES -> "cm"
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (state.shape) {
            ShapeKind.CIRCLE -> DimensionField(
                label = "Diameter ($unitLabel)",
                value = state.diameter,
                error = state.errors[DimensionField.DIAMETER.name],
                onChange = { viewModel.setDimension(DimensionField.DIAMETER, it) },
            )
            ShapeKind.OVAL -> {
                DimensionField(
                    label = "Length ($unitLabel)",
                    value = state.ovalLength,
                    error = state.errors[DimensionField.OVAL_LENGTH.name],
                    onChange = { viewModel.setDimension(DimensionField.OVAL_LENGTH, it) },
                )
                DimensionField(
                    label = "Width ($unitLabel)",
                    value = state.ovalWidth,
                    error = state.errors[DimensionField.OVAL_WIDTH.name],
                    onChange = { viewModel.setDimension(DimensionField.OVAL_WIDTH, it) },
                )
            }
            ShapeKind.RECTANGLE -> {
                DimensionField(
                    label = "Width ($unitLabel)",
                    value = state.rectWidth,
                    error = state.errors[DimensionField.RECT_WIDTH.name],
                    onChange = { viewModel.setDimension(DimensionField.RECT_WIDTH, it) },
                )
                DimensionField(
                    label = "Height ($unitLabel)",
                    value = state.rectHeight,
                    error = state.errors[DimensionField.RECT_HEIGHT.name],
                    onChange = { viewModel.setDimension(DimensionField.RECT_HEIGHT, it) },
                )
            }
            ShapeKind.SQUARE -> DimensionField(
                label = "Side ($unitLabel)",
                value = state.side,
                error = state.errors[DimensionField.SIDE.name],
                onChange = { viewModel.setDimension(DimensionField.SIDE, it) },
            )
        }
    }
}

@Composable
private fun DimensionField(
    label: String,
    value: String,
    error: String?,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
