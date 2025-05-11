package com.example.curiocity.presentation

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.curiocity.R
import com.example.curiocity.databinding.ActivityMainBinding
import com.example.curiocity.presentation.architecture.activity.CurioActivity
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import com.example.curiocity.presentation.ui.username.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : CurioActivity<ActivityMainBinding, CurioViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        openView(LoginFragment::class)
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun getViewModelClass(): Class<CurioViewModel> = CurioViewModel::class.java

    override fun getContainerViewId() = R.id.fragment_container_main

}