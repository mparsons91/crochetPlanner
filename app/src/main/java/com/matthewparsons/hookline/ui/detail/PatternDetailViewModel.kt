package com.matthewparsons.hookline.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matthewparsons.hookline.domain.repository.PatternRepository
import com.matthewparsons.hookline.domain.repository.SavedPattern
import com.matthewparsons.hookline.ui.navigation.HooklineDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PatternDetailUiState {
    data object Loading : PatternDetailUiState
    data object NotFound : PatternDetailUiState
    data class Loaded(val saved: SavedPattern) : PatternDetailUiState
}

@HiltViewModel
class PatternDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PatternRepository,
) : ViewModel() {

    private val id: String = checkNotNull(
        savedStateHandle[HooklineDestinations.PATTERN_DETAIL_ARG_ID]
    ) { "Missing pattern id in nav args" }

    private val _state = MutableStateFlow<PatternDetailUiState>(PatternDetailUiState.Loading)
    val state: StateFlow<PatternDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch { reload() }
    }

    fun toggleStep(index: Int) {
        val current = _state.value as? PatternDetailUiState.Loaded ?: return
        val saved = current.saved
        val newIndices = if (index in saved.completedStepIndices) {
            saved.completedStepIndices - index
        } else {
            saved.completedStepIndices + index
        }
        // Optimistic local update so the UI reacts immediately.
        _state.value = PatternDetailUiState.Loaded(saved.copy(completedStepIndices = newIndices))
        viewModelScope.launch {
            repository.updateCompletedSteps(saved.id, newIndices)
        }
    }

    fun delete(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.delete(id)
            onComplete()
        }
    }

    private suspend fun reload() {
        val saved = repository.getById(id)
        _state.value = saved?.let(PatternDetailUiState::Loaded) ?: PatternDetailUiState.NotFound
    }
}
