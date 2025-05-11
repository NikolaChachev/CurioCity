package com.example.curiocity.presentation.ui.home

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.example.curiocity.data.local.entity.UserEntity
import com.example.curiocity.data.repository.GameRepository
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import com.example.curiocity.presentation.ui.GameFragment.Companion.LEVEL_NUMBER_KEY
import com.example.curiocity.presentation.ui.GameFragment.Companion.QUESTION_NUMBER_KEY
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

    private lateinit var currentUser: UserEntity

    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    override fun receiveNavigationArgs(args: Bundle?) {
        args?.let {
            if (it.containsKey(USER_BUNDLE_KEY)) {
                val user = it.getParcelable<UserEntity>(USER_BUNDLE_KEY)
                    ?: throw IllegalStateException("User cannot be null at this point")
                currentUser = user
                _currentLevel.value = user.currentLevel
            }
        }
    }

    override fun postNavigationArgs(): Bundle? {
        val bundle = Bundle().apply {
            putInt(LEVEL_NUMBER_KEY, currentUser.currentLevel)
            putInt(QUESTION_NUMBER_KEY, currentUser.currentQuestion)
        }
        return bundle
    }

    init {
        viewModelScope.launch {
            gameRepository.fetchLevelsData()
        }
    }

    companion object {
        const val USER_BUNDLE_KEY = "user_id"
    }
}