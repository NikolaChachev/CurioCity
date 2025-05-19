package com.example.curiocity.presentation.ui.question

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.lifecycleScope
import com.example.curiocity.BR
import com.example.curiocity.R
import com.example.curiocity.databinding.FragmentGameBinding
import com.example.curiocity.presentation.architecture.fragment.CurioFragment
import com.example.curiocity.presentation.ui.custom.CooldownBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameFragment : CurioFragment<FragmentGameBinding, GameViewModel>() {

    private lateinit var animator: ValueAnimator

    override fun onPrepareLayout(layoutView: View?) {
        setupOnClicks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        startCooldown(binding.gameTimerBar)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.answerState.collect { state ->
                when (state) {
                    is AnswerState.AnsweredCorrectly -> {
                        launchDialogFragment(
                            "Correct!",
                            "You answered correctly and gained 5 points, " +
                                    "do you want to continue?",
                            "Continue",
                            "Go Home",
                            primaryClick = {
                                viewModel.loadNextQuestion()
                                animator.start()
                            },
                            secondaryClick = {
                                navigateBack()
                            }
                        )
                    }

                    is AnswerState.AnsweredIncorrectly -> {
                        launchDialogFragment(
                            "Wrong!",
                            "Unfortunately that was the wrong answer!",
                            "Try again",
                            "Go Home",
                            primaryClick = {
                                animator.start()
                            },
                            secondaryClick = {
                                navigateBack()
                            }
                        )
                    }

                    is AnswerState.NoMoreQuestions -> {
                        launchDialogFragment(
                            "You finished the level!",
                            "Do you want to continue to next level or go home?",
                            "Continue",
                            "Go Home",
                            primaryClick = {
                                updateUI(false)
                                loadNextLevel()
                            },
                            secondaryClick = {
                                navigateBack()
                            }
                        )
                    }

                    is AnswerState.GameFinished -> {
                        launchDialogFragment(
                            "You finished the game!",
                            "Congrats! You look smart now!",
                            "Go Home",
                            null,
                            primaryClick = {
                                navigateBack()
                            },
                            null
                        )
                    }

                    is AnswerState.Loading -> {
                        updateUI(false)
                    }

                    is AnswerState.Loaded -> {
                        updateUI(true)
                    }
                }
            }
        }
    }

    private fun loadNextLevel() {
        viewModel.loadNextLevel()
    }

    private fun updateUI(showUI: Boolean) {
        with(binding) {
            if (showUI) {
                gameProgressBar.visibility = View.GONE
                gameQuestionText.visibility = View.VISIBLE
                gameTimerBar.visibility = View.VISIBLE
                gameAnswer1.visibility = View.VISIBLE
                gameAnswer2.visibility = View.VISIBLE
                gameAnswer3.visibility = View.VISIBLE
                gameAnswer4.visibility = View.VISIBLE
            } else {
                gameProgressBar.visibility = View.VISIBLE
                gameQuestionText.visibility = View.GONE
                gameTimerBar.visibility = View.GONE
                gameAnswer1.visibility = View.GONE
                gameAnswer2.visibility = View.GONE
                gameAnswer3.visibility = View.GONE
                gameAnswer4.visibility = View.GONE
            }
        }

    }


    private fun startCooldown(bar: CooldownBarView) {
        animator = ValueAnimator.ofFloat(1f, 0f)
        animator.duration = TIMER_DURATION_IN_SECONDS
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            bar.progress = animation.animatedValue as Float
            if (animation.animatedValue as Float == 0f) {
                onTimerEnd()
            }
        }
        animator.start()
    }

    private fun onTimerEnd() {
        launchDialogFragment(
            "Too slow!",
            "You couldn't answer in time!",
            "Try again",
            "Go Home",
            primaryClick = {
                animator.start()
            },
            secondaryClick = {
                navigateBack()
            }
        )
    }

    private fun setupOnClicks() {
        with(binding) {
            gameAnswer1.setOnClickListener {
                viewModel.checkAnswer(gameAnswer1.text.toString())
            }
            gameAnswer2.setOnClickListener {
                viewModel.checkAnswer(gameAnswer2.text.toString())
            }
            gameAnswer3.setOnClickListener {
                viewModel.checkAnswer(gameAnswer3.text.toString())
            }
            gameAnswer4.setOnClickListener {
                viewModel.checkAnswer(gameAnswer4.text.toString())
            }
        }
    }

    private fun launchDialogFragment(
        title: String,
        message: String,
        primaryText: String,
        secondaryText: String?,
        primaryClick: () -> Unit,
        secondaryClick: (() -> Unit)?
    ) {
        animator.pause()
        val dialog = QuestionResultDialog(
            title,
            message,
            primaryText,
            secondaryText,
            onPrimaryClick = primaryClick,
            onSecondaryClick = secondaryClick
        )
        dialog.show(parentFragmentManager, "customDialog")
    }

    override fun getViewModelResId(): Int = BR.gameVM

    override fun getLayoutResId(): Int = R.layout.fragment_game

    override fun getViewModelClass(): Class<GameViewModel> = GameViewModel::class.java

    companion object {
        const val TIMER_DURATION_IN_SECONDS = 20 * 1000L
    }
}