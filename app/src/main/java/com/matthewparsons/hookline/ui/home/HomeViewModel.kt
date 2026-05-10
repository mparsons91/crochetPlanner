package com.matthewparsons.hookline.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matthewparsons.hookline.domain.repository.PatternRepository
import com.matthewparsons.hookline.domain.repository.SavedPattern
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: PatternRepository,
) : ViewModel() {

    val patterns: StateFlow<List<SavedPattern>> = repository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
