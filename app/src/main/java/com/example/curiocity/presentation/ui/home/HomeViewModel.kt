package com.example.curiocity.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.curiocity.data.repository.GameRepository
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import com.example.curiocity.presentation.ui.ResourcesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : CurioViewModel(), ResourcesDataSource {

    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()
    override val currentLives: LiveData<Int> = gameRepository.currentLives
    private val _playerScore = MutableLiveData<Int>()
    override val playerScore: LiveData<Int> = gameRepository.playerScore


    fun updateData() {
        viewModelScope.launch {
            gameRepository.fetchLevelsData()
            _currentLevel.emit(gameRepository.currentUser.currentLevel)
            _playerScore.postValue(gameRepository.currentUser.currentScore)
        }
    }
}