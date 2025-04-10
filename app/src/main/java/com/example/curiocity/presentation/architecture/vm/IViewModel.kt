package com.example.curiocity.presentation.architecture.vm

import android.os.Bundle
import com.example.curiocity.presentation.architecture.activity.CurioActivity
import com.example.curiocity.presentation.architecture.fragment.CurioFragment


interface IViewModel {


    /**
     * This method is called when view model is resumed.
     * The view model lifecycle is bound to the views lifecycle.
     * Note that if the view is activity
     * this method will be called from the  [CurioActivity.onResume].
     * If the view is fragment this method may be called
     * from [CurioFragment.onResume] or [CurioFragment.onHiddenChanged].
     */
    fun onResume()

    /**
     * This method is called when view model is paused.
     * The view model lifecycle is bound to the views lifecycle.
     * Note that if the view is of type activity
     * this method will be called from the [CurioActivity.onPause].
     * If the view is of type fragment this method may be called
     * from [CurioFragment.onPause] or [CurioFragment.onHiddenChanged].
     */
    fun onPause()

    /**
     * Override this method if you want to pass arguments
     * to the next screen that is about to open.
     *
     * @return the arguments that will be passed
     */
    fun postNavigationArgs(): Bundle?

    /**
     * Override this method to receive navigation arguments
     * when this view model is resumed.
     *
     * @param args the arguments that are passed, this may be null
     */
    fun receiveNavigationArgs(args: Bundle?)
}