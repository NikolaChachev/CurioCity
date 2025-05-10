package com.example.curiocity.presentation

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.curiocity.R
import com.example.curiocity.databinding.ActivityMainBinding
import com.example.curiocity.presentation.architecture.activity.CurioActivity
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : CurioActivity<ActivityMainBinding, CurioViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val navView: BottomNavigationView = binding.navView

        val navController: NavController by lazy {
            val navHost = supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment_activity_main
            ) as NavHostFragment
            navHost.navController
        }
        navView.setupWithNavController(navController)
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun getViewModelClass(): Class<CurioViewModel> = CurioViewModel::class.java

    override fun getContainerViewId() = R.id.nav_host_fragment_activity_main
}