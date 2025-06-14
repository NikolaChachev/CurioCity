package com.example.curiocity.presentation.ui.question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.curiocity.data.local.entity.LevelEntity
import com.example.curiocity.data.repository.GameRepository
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import com.example.curiocity.presentation.ui.ResourcesDataSource
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
    data object NoMoreLives : AnswerState()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : CurioViewModel(), ResourcesDataSource {

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

    override val currentLives: LiveData<Int> = gameRepository.currentLives

    override val playerScore: LiveData<Int> = gameRepository.playerScore

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

    fun loadNextLevel() {
        viewModelScope.launch {
            getLevelData()
        }
    }

    fun checkAnswer(givenAnswer: String) {
        if (correctAnswer == givenAnswer)
            handleCorrectAnswer()
        else
            handleWrongAnswer()
    }

    private fun handleCorrectAnswer() {
        viewModelScope.launch {
            gameRepository.updateUserScore(CORRECT_ANSWER_POINTS)
            val state =
                if (currentQuestionIndex == levelEntity.questions.size - 1) {
                    currentLevel++
                    currentQuestionIndex = 0
                    AnswerState.NoMoreQuestions
                } else {
                    currentQuestionIndex++
                    AnswerState.AnsweredCorrectly
                }
            gameRepository.updateUserLevel(currentLevel)
            gameRepository.updateUserQuestion(currentQuestionIndex + 1)
            _answerState.emit(state)
        }
    }

    private fun handleWrongAnswer() {
        viewModelScope.launch {
            gameRepository.updateUserScore(WRONG_ANSWER_POINTS)
            gameRepository.removeLifeFromPlayer()
            if (gameRepository.currentLives.value == 0)
                _answerState.emit(AnswerState.NoMoreLives)
            else
                _answerState.emit(AnswerState.AnsweredIncorrectly)
        }
    }

    companion object {
        const val CORRECT_ANSWER_POINTS = 5
        const val WRONG_ANSWER_POINTS = -10

    }
} 