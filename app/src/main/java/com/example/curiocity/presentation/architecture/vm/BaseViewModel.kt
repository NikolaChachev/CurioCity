package com.example.curiocity.presentation.architecture.vm

import android.os.Bundle
import androidx.lifecycle.ViewModel

abstract class BaseViewModel: ViewModel(), IViewModel {

    override fun postNavigationArgs(): Bundle? {
        //do nothing
        return null
    }

    override fun receiveNavigationArgs(args: Bundle?) {
        //do nothing
    }

    override fun onResume() {
        // implement when needed
    }

    override fun onPause() {
        // implement when needed
    }
}