package com.matthewparsons.hookline.ui.newpattern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matthewparsons.hookline.domain.model.HookSize
import com.matthewparsons.hookline.domain.model.Length
import com.matthewparsons.hookline.domain.model.LengthUnit
import com.matthewparsons.hookline.domain.model.PatternInput
import com.matthewparsons.hookline.domain.model.Shape
import com.matthewparsons.hookline.domain.model.YarnWeight
import com.matthewparsons.hookline.domain.pattern.PatternEngine
import com.matthewparsons.hookline.domain.repository.PatternRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ShapeKind(val displayName: String) {
    CIRCLE("Circle"),
    OVAL("Oval"),
    RECTANGLE("Rectangle"),
    SQUARE("Square"),
}

enum class DimensionField {
    DIAMETER, OVAL_LENGTH, OVAL_WIDTH, RECT_WIDTH, RECT_HEIGHT, SIDE,
}

data class NewPatternUiState(
    val yarn: YarnWeight = YarnWeight.MEDIUM,
    val hookSizeMm: String = "5.5",
    val shape: ShapeKind = ShapeKind.CIRCLE,
    val unit: LengthUnit = LengthUnit.INCHES,
    val diameter: String = "",
    val ovalLength: String = "",
    val ovalWidth: String = "",
    val rectWidth: String = "",
    val rectHeight: String = "",
    val side: String = "",
    val isSubmitting: Boolean = false,
    val errors: Map<String, String> = emptyMap(),
    val submitError: String? = null,
    val savedPatternId: String? = null,
)

@HiltViewModel
class NewPatternViewModel @Inject constructor(
    private val repository: PatternRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NewPatternUiState())
    val state: StateFlow<NewPatternUiState> = _state.asStateFlow()

    fun setYarn(yarn: YarnWeight) = _state.update { it.copy(yarn = yarn) }
    fun setHookSize(value: String) = _state.update { it.copy(hookSizeMm = value, errors = it.errors - "hook") }
    fun setShape(shape: ShapeKind) = _state.update { it.copy(shape = shape, errors = emptyMap()) }
    fun setUnit(unit: LengthUnit) = _state.update { it.copy(unit = unit) }

    fun setDimension(field: DimensionField, value: String) = _state.update { current ->
        val cleared = current.errors - field.name
        when (field) {
            DimensionField.DIAMETER -> current.copy(diameter = value, errors = cleared)
            DimensionField.OVAL_LENGTH -> current.copy(ovalLength = value, errors = cleared)
            DimensionField.OVAL_WIDTH -> current.copy(ovalWidth = value, errors = cleared)
            DimensionField.RECT_WIDTH -> current.copy(rectWidth = value, errors = cleared)
            DimensionField.RECT_HEIGHT -> current.copy(rectHeight = value, errors = cleared)
            DimensionField.SIDE -> current.copy(side = value, errors = cleared)
        }
    }

    fun submit() {
        val current = _state.value
        val errors = validate(current)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(errors = errors) }
            return
        }
        val input = buildPatternInput(current)
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submitError = null) }
            runCatching {
                val pattern = PatternEngine.generate(input)
                repository.save(pattern)
            }.onSuccess { saved ->
                _state.update { it.copy(isSubmitting = false, savedPatternId = saved.id) }
            }.onFailure { t ->
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = t.localizedMessage ?: "Could not generate pattern.",
                    )
                }
            }
        }
    }

    fun consumeNavigationEvent() = _state.update { it.copy(savedPatternId = null) }

    private fun validate(s: NewPatternUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        validatePositive(s.hookSizeMm, "hook", errors)
        when (s.shape) {
            ShapeKind.CIRCLE -> validatePositive(s.diameter, DimensionField.DIAMETER.name, errors)
            ShapeKind.OVAL -> {
                validatePositive(s.ovalLength, DimensionField.OVAL_LENGTH.name, errors)
                validatePositive(s.ovalWidth, DimensionField.OVAL_WIDTH.name, errors)
                val len = s.ovalLength.toDoubleOrNull()
                val wid = s.ovalWidth.toDoubleOrNull()
                if (len != null && wid != null && len <= wid) {
                    errors[DimensionField.OVAL_WIDTH.name] = "Must be smaller than length"
                }
            }
            ShapeKind.RECTANGLE -> {
                validatePositive(s.rectWidth, DimensionField.RECT_WIDTH.name, errors)
                validatePositive(s.rectHeight, DimensionField.RECT_HEIGHT.name, errors)
            }
            ShapeKind.SQUARE -> validatePositive(s.side, DimensionField.SIDE.name, errors)
        }
        return errors
    }

    private fun validatePositive(value: String, key: String, errors: MutableMap<String, String>) {
        val parsed = value.toDoubleOrNull()
        when {
            value.isBlank() -> errors[key] = "Required"
            parsed == null -> errors[key] = "Must be a number"
            parsed <= 0.0 -> errors[key] = "Must be greater than 0"
        }
    }

    private fun buildPatternInput(s: NewPatternUiState): PatternInput {
        val unit = s.unit
        val hookMm = s.hookSizeMm.toDouble()
        val shape: Shape = when (s.shape) {
            ShapeKind.CIRCLE -> Shape.Circle(Length(s.diameter.toDouble(), unit))
            ShapeKind.OVAL -> Shape.Oval(
                length = Length(s.ovalLength.toDouble(), unit),
                width = Length(s.ovalWidth.toDouble(), unit),
            )
            ShapeKind.RECTANGLE -> Shape.Rectangle(
                width = Length(s.rectWidth.toDouble(), unit),
                height = Length(s.rectHeight.toDouble(), unit),
            )
            ShapeKind.SQUARE -> Shape.Square(Length(s.side.toDouble(), unit))
        }
        return PatternInput(shape = shape, yarn = s.yarn, hook = HookSize(hookMm))
    }
}
