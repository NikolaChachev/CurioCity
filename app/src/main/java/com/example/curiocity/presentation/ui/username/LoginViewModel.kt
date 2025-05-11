package com.example.curiocity.presentation.ui.username

import androidx.lifecycle.viewModelScope
import com.example.curiocity.data.local.SharedPreferencesManager
import com.example.curiocity.data.local.entity.UserEntity
import com.example.curiocity.data.repository.GameRepository
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : CurioViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    private val cachedUsers = mutableListOf<UserEntity>()

    fun checkForExistingUser() {
        viewModelScope.launch {
            val currentUser = sharedPreferencesManager.getUserUUID()
            cachedUsers.addAll(gameRepository.fetchUsers())
            if (currentUser.isNullOrEmpty()) {
                _loginState.value = LoginState.Initial
                return@launch
            }

            val user = cachedUsers.find { it.uuid == currentUser }
            if (user == null) {
                _loginState.value = LoginState.Initial
                return@launch
            }
            _loginState.value = LoginState.Success(user)
        }
    }

    fun checkUsername(username: String) {
        if (username.isBlank()) {
            _loginState.value = LoginState.Error("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val isAvailable = cachedUsers.find { it.username == username } == null
                if (isAvailable) {
                    val user = gameRepository.createUser(username)
                    sharedPreferencesManager.saveUserUUID(user.uuid)
                    _loginState.value = LoginState.Success(user)
                } else {
                    _loginState.value = LoginState.Error("Username already exists")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("An error occurred, please try again")
            } finally {
                gameRepository.deleteUserByUsername(username)
            }
        }
    }
}

sealed class LoginState {
    data object Initial : LoginState()
    data object Loading : LoginState()
    data class Success(val user: UserEntity) : LoginState()
    data class Error(val message: String) : LoginState()
} 