package com.example.curiocity.presentation.ui.home

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import com.example.curiocity.R
import com.example.curiocity.databinding.FragmentHomeBinding
import com.example.curiocity.presentation.architecture.fragment.CurioFragment
import com.example.curiocity.presentation.ui.question.GameFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : CurioFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun onPrepareLayout(layoutView: View?) {
        binding.apply {
            setImageToLocked(homeFragmentLevel2)
            setImageToLocked(homeFragmentLevel3)
            homePlayButton.setOnClickListener {
                navigateToView(GameFragment::class)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentLevel.collectLatest { level ->
                when (level) {
                    1 -> updatePlayButtonConstraints(binding.homeFragmentLevel1)
                    2 -> {
                        setImageToUnlocked(binding.homeFragmentLevel2)
                        updatePlayButtonConstraints(binding.homeFragmentLevel2)
                    }
                    3 -> {
                        setImageToUnlocked(binding.homeFragmentLevel2)
                        setImageToUnlocked(binding.homeFragmentLevel3)
                        updatePlayButtonConstraints(binding.homeFragmentLevel3)
                    }
                }
            }
        }
    }

    private fun setImageToLocked(view: ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val cf = ColorMatrixColorFilter(matrix)
        view.colorFilter = cf
        view.imageAlpha = 128
    }

    private fun setImageToUnlocked(view: ImageView) {
        view.colorFilter = null
        view.imageAlpha = 255
    }

    private fun updatePlayButtonConstraints(view: ImageView) {
        binding.apply {
            val constraintSet = ConstraintSet()
            constraintSet.clone(homeLayout)

            constraintSet.connect(
                homePlayButton.id,
                ConstraintSet.TOP,
                view.id,
                ConstraintSet.BOTTOM
            )

            constraintSet.connect(
                homePlayButton.id,
                ConstraintSet.START,
                view.id,
                ConstraintSet.START
            )
            constraintSet.connect(
                homePlayButton.id,
                ConstraintSet.END,
                view.id,
                ConstraintSet.END
            )

            // Apply the changes
            constraintSet.applyTo(homeLayout)
        }
    }

    override fun getViewModelResId(): Int? = null

    override fun getLayoutResId(): Int = R.layout.fragment_home

    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java
}