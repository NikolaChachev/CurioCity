package com.example.curiocity.presentation.ui

import androidx.lifecycle.LiveData

interface ResourcesDataSource {

    val currentLives: LiveData<Int>
    val playerScore: LiveData<Int>
}