package com.example.curiocity.presentation.ui.question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.curiocity.data.local.entity.LevelEntity
import com.example.curiocity.data.repository.GameRepository
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AnswerState {
    data object AnsweredCorrectly : AnswerState()
    data object AnsweredIncorrectly : AnswerState()
    data object Loading : AnswerState()
    data object NoMoreQuestions : AnswerState()
    data object GameFinished : AnswerState()
    data object Loaded : AnswerState()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : CurioViewModel() {

    private val _gameLevelInfo = MutableLiveData("")
    val gameLevelInfo: LiveData<String> = _gameLevelInfo
    private val _questionString = MutableLiveData("")
    val questionString: LiveData<String> = _questionString
    private val _answerA = MutableLiveData("")
    val answerA: LiveData<String> = _answerA
    private val _answerB = MutableLiveData("")
    val answerB: LiveData<String> = _answerB
    private val _answerC = MutableLiveData("")
    val answerC: LiveData<String> = _answerC
    private val _answerD = MutableLiveData("")
    val answerD: LiveData<String> = _answerD

    private val _answerState = MutableSharedFlow<AnswerState>().apply { AnswerState.Loading }
    val answerState: SharedFlow<AnswerState> = _answerState.asSharedFlow()

    private var accumulatedScore = 0
    private var correctAnswer: String = ""
    private var currentLevel = 1
    private var currentQuestionIndex = 1
    private lateinit var levelEntity: LevelEntity

    init {
        val user = gameRepository.currentUser
        currentLevel = user.currentLevel
        currentQuestionIndex = user.currentQuestion - 1
        getLevelData()
    }

    override fun onPause() {
        viewModelScope.launch {
            gameRepository.updateUserScore(accumulatedScore)
        }
    }

    private fun getLevelData() {
        viewModelScope.launch {
            val level = gameRepository.getCurrentLevelData(currentLevel)
            if (level == null) {
                _answerState.emit(AnswerState.GameFinished)
                return@launch
            }
            gameRepository.updateUserLevel(currentLevel)
            levelEntity = level
            val currentQuestion = levelEntity.questions[currentQuestionIndex]
            with(currentQuestion) {
                correctAnswer = answer
                _questionString.postValue(question)
                _answerA.postValue(a)
                _answerB.postValue(b)
                _answerC.postValue(c)
                _answerD.postValue(d)
            }
            val infoString = "Level: ${levelEntity.name}\nQuestion: ${currentQuestionIndex + 1}"
            _gameLevelInfo.postValue(infoString)
            _answerState.emit(AnswerState.Loaded)
        }
    }

    fun loadNextQuestion() {
        viewModelScope.launch {
            currentQuestionIndex++
            val currentQuestion = levelEntity.questions[currentQuestionIndex]
            gameRepository.updateUserQuestion(currentQuestionIndex)
            with(currentQuestion) {
                correctAnswer = answer
                _questionString.postValue(question)
                _answerA.postValue(a)
                _answerB.postValue(b)
                _answerC.postValue(c)
                _answerD.postValue(d)
            }
            val infoString = "Level: ${levelEntity.name}\nQuestion: ${currentQuestionIndex + 1}"
            _gameLevelInfo.postValue(infoString)

            _answerState.emit(AnswerState.Loaded)
        }
    }

    fun loadNextLevel() {
        viewModelScope.launch {
            currentLevel++
            getLevelData()
        }
    }

    fun checkAnswer(givenAnswer: String) {
        viewModelScope.launch {
            if (correctAnswer == givenAnswer) {
                accumulatedScore += CORRECT_ANSWER_POINTS
                val state =
                    if (currentQuestionIndex == levelEntity.questions.size - 1)
                        AnswerState.NoMoreQuestions
                    else
                        AnswerState.AnsweredCorrectly
                _answerState.emit(state)
            } else {
                accumulatedScore -= WRONG_ANSWER_POINTS
                _answerState.emit(AnswerState.AnsweredIncorrectly)
            }
        }
    }

    companion object {
        const val CORRECT_ANSWER_POINTS = 5
        const val WRONG_ANSWER_POINTS = 10

    }
} 