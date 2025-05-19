package com.example.curiocity.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.example.curiocity.data.repository.GameRepository
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : CurioViewModel() {

    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.fetchLevelsData()
            _currentLevel.emit(gameRepository.currentUser.currentLevel)
        }
    }
}