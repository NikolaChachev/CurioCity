package com.example.curiocity.presentation.ui.username

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.curiocity.R
import com.example.curiocity.databinding.FragmentLoginBinding
import com.example.curiocity.presentation.architecture.fragment.CurioFragment
import com.example.curiocity.presentation.ui.home.HomeFragment
import com.example.curiocity.presentation.ui.home.HomeViewModel.Companion.USER_BUNDLE_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : CurioFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeState()
        viewModel.checkForExistingUser()
    }

    private fun setupUI() {
        binding.submitButton.setOnClickListener {
            val username = binding.usernameInput.text?.toString() ?: ""
            viewModel.checkUsername(username)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is LoginState.Initial -> {
                            binding.progressBar.isVisible = false
                            binding.errorText.isVisible = false
                            binding.submitButton.isVisible = true
                            binding.usernameInputLayout.isVisible = true
                        }

                        is LoginState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.errorText.isVisible = false
                            binding.submitButton.isVisible = false
                            binding.usernameInputLayout.isVisible = false
                        }

                        is LoginState.Success -> {
                            // Navigate to the next screen
                            val bundle = Bundle().apply {
                                putParcelable(
                                    USER_BUNDLE_KEY,
                                    state.user
                                )
                            }
                            navigateToView(HomeFragment::class, bundle)
                        }

                        is LoginState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.errorText.apply {
                                text = state.message
                                isVisible = true
                            }
                            binding.usernameInputLayout.isVisible = true
                            binding.submitButton.isVisible = true
                        }
                    }
                }
            }
        }
    }

    override fun getViewModelResId(): Int? = null

    override fun getLayoutResId(): Int = R.layout.fragment_login

    override fun getViewModelClass(): Class<LoginViewModel> = LoginViewModel::class.java
} 