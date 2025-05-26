package com.example.curiocity.presentation

import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.curiocity.BR
import com.example.curiocity.R
import com.example.curiocity.databinding.ActivityMainBinding
import com.example.curiocity.presentation.architecture.activity.CurioActivity
import com.example.curiocity.presentation.ui.home.HomeFragment
import com.example.curiocity.presentation.ui.username.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : CurioActivity<ActivityMainBinding, MainViewModel>() {

    private var isAppJustLaunched = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        openView(LoginFragment::class)
        binding.setVariable(
            BR.mainVM,
            viewModel
        )
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java
    override fun onViewChanged(viewClass: String) {
        if (viewClass == HomeFragment::class.qualifiedName) {
            if (isAppJustLaunched) {
                viewModel.loadLifeData()
                viewModel.startLifeRegenerationTimer()
                isAppJustLaunched = false
            }
            binding.lifeTimer.visibility = View.VISIBLE

        } else {
            binding.lifeTimer.visibility = View.INVISIBLE
        }
    }

    override fun getContainerViewId() = R.id.fragment_container_main

}