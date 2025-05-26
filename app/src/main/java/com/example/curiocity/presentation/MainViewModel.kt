package com.example.curiocity.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.curiocity.data.repository.GameRepository
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : CurioViewModel() {

    private val _timeUntilNextLife = MutableLiveData<String>()
    val timeUntilNextLife: LiveData<String> = _timeUntilNextLife

    private var lastLifeUpdateTime = gameRepository.loadLastCloseTimer()
    private var remainingTimeInMillis = gameRepository.loadLifeTimer()

    fun loadLifeData() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            var accumulatedTime = currentTime - lastLifeUpdateTime
            var currentLives = gameRepository.currentUser.lives
            while (accumulatedTime > LIFE_REGEN_INTERVAL && currentLives < 5) {
                currentLives++
                accumulatedTime -= LIFE_REGEN_INTERVAL
            }
            remainingTimeInMillis -= accumulatedTime

            gameRepository.updateUserLives(currentLives)
        }
    }

    fun startLifeRegenerationTimer() {
        viewModelScope.launch {

            while (true) {
                if (remainingTimeInMillis <= 0) {
                    remainingTimeInMillis = LIFE_REGEN_INTERVAL
                    val currentLives = gameRepository.currentLives.value ?: 0
                    if (currentLives < 5)
                        gameRepository.updateUserLives(currentLives + 1)
                }

                val minutes = (remainingTimeInMillis / MINUTE).toInt()
                val seconds = ((remainingTimeInMillis % MINUTE) / SECOND).toInt()
                _timeUntilNextLife.postValue(
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        seconds
                    )
                )
                remainingTimeInMillis -= SECOND
                delay(SECOND) // Update every second
            }
        }
    }

    override fun onPause() {
        super.onPause()
        gameRepository.saveCurrentTime()
        gameRepository.saveLifeTimer(remainingTimeInMillis)
    }

    companion object {
        const val LIFE_REGEN_INTERVAL = 120_000L // 2 minutes in milliseconds
        private const val MINUTE = 60_000L
        private const val SECOND = 1_000L
    }
}