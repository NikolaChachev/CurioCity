package com.example.curiocity.presentation.architecture.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.curiocity.presentation.architecture.fragment.CurioFragment
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import kotlin.reflect.KClass

abstract class CurioActivity<B : ViewDataBinding, VM : CurioViewModel> : AppCompatActivity() {
    private var numOfBackPressed = 0
    private var currentView: CurioFragment<*, *>? = null

    // controls whether the back navigation should skip sending the message and execute the onBack
    private var skipBackMsg = false

    protected lateinit var binding: B
    protected val viewModel by lazy { ViewModelProvider(this)[getViewModelClass()] }
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backPressed()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        onBackPressedDispatcher.addCallback(this, callback)

    }

    protected abstract fun getLayoutId(): Int
    protected abstract fun getViewModelClass(): Class<VM>

    //region public methods

    fun createShortToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    //endregion

    //region Base methods for activities

    fun backPressed() {
        if (currentView == null) {
            handleBackPress()
        } else {
            navigateBack()
        }
    }

    fun <T : AppCompatActivity> openActivity(activityClass: KClass<T>, args: Bundle? = null) {
        hideKeyboard()

        val resolvedArgs = resolveArgs(args, viewModel)
        //pass current activity stack request
        val keepInStack: Boolean = keepInStack()
        resolvedArgs.putBoolean(KEEP_IN_STACK_KEY, keepInStack)

        //start the new activity
        val intent = Intent(this, activityClass.java)
        intent.putExtra(ACTIVITY_BUNDLE_EXTRA_KEY, args)
        startActivity(intent)

        //finish current activity if we don't want it in the stack
        if (!keepInStack) {
            finish()
        }
    }

    fun <T : CurioFragment<*, *>> openView(viewClass: KClass<T>, args: Bundle? = null) {
        hideKeyboard()

        val containerViewId = getContainerViewId()
        if (containerViewId == 0) {
            throw RuntimeException("MISSING VIEW CONTAINER ID")
        }

        val viewName = viewClass.qualifiedName ?: return

        var currentViewClassName: String? = null
        currentView?.let {
            currentViewClassName = it::class.qualifiedName
        }
        if (currentViewClassName != viewName) {
            val fm = supportFragmentManager
            val existing = fm.findFragmentByTag(viewName)
            val newView: CurioFragment<*, *>? = if (existing != null) {
                popBackStackTo(fm, viewName, args)
            } else {
                attachNewView(fm, containerViewId, viewName, args)
            }
            currentView = newView
        }
    }

    private fun popBackStackTo(
        fragmentManager: FragmentManager,
        viewClassName: String,
        args: Bundle?
    ): CurioFragment<*, *>? {
        val view = fragmentManager.findFragmentByTag(viewClassName) ?: return null

        val existingArgs = view.arguments
        if (existingArgs != null && args != null) {
            existingArgs.putAll(args)
        } else {
            view.arguments = args
        }

        fragmentManager.popBackStack(viewClassName, 0)
        fragmentManager.executePendingTransactions()
        return view as CurioFragment<*, *>
    }

    private fun attachNewView(
        fragmentManager: FragmentManager,
        containerId: Int,
        viewClassName: String,
        args: Bundle?
    ): CurioFragment<*, *> {

        val fact = fragmentManager.fragmentFactory

        val newFragment = fact.instantiate(classLoader, viewClassName)
        if (args != null) {
            newFragment.arguments = args
        }
        val ftx = fragmentManager.beginTransaction()

        ftx.replace(containerId, newFragment, viewClassName)
        ftx.addToBackStack(viewClassName)
        ftx.commit()
        fragmentManager.executePendingTransactions()

        return newFragment as CurioFragment<*, *>
    }

    private fun navigateBack() {
        hideKeyboard()

        val fm = supportFragmentManager
        val lastBackStackEntryIndex = fm.backStackEntryCount - 1
        if (lastBackStackEntryIndex <= 0) {
            handleFinishAfterTransition()
        } else {
            var lastTaggedEntryIndex = lastBackStackEntryIndex - 1
            var backStackEntry: FragmentManager.BackStackEntry

            do {
                // search for a non-null tag for entries above the bottom one
                backStackEntry = fm.getBackStackEntryAt(lastTaggedEntryIndex)
            } while ((backStackEntry.name == null) && ((lastTaggedEntryIndex--) > 0))

            val viewClassName = backStackEntry.name
            if (viewClassName != null) {
                val newView = popBackStackTo(fm, viewClassName, null)
                currentView = newView
            }
        }
    }

    //handles back presses while there are fragments in the backstack of the activity
    private fun handleBackPress() {
        ++numOfBackPressed
        if (numOfBackPressed == MAX_NUM_OF_BACK_PRESSES || skipBackMsg) {
            onBackPressedDispatcher.onBackPressed()
            callback.isEnabled = false
        } else {
            handleExitMsg()
        }
    }

    //handles back presses while the activity has only one fragment in the backstack
    private fun handleFinishAfterTransition() {
        ++numOfBackPressed
        if (numOfBackPressed == MAX_NUM_OF_BACK_PRESSES || skipBackMsg) {
            supportFinishAfterTransition()
        } else {
            handleExitMsg()
        }
    }

    private fun handleExitMsg() {
        val exitMsg = "press again to exit"
        Toast.makeText(this, exitMsg, Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ numOfBackPressed = 0 }, EXIT_MSG_DELAY_TIME)
    }

    open fun getContainerViewId(): Int = 0

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = currentFocus
        if (currentFocus != null) {
            val windowToken = currentFocus.windowToken
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    private fun resolveArgs(viewArgs: Bundle?, vm: VM): Bundle {
        val args = Bundle()
        if (viewArgs != null) {
            args.putAll(viewArgs)
        }
        vm.postNavigationArgs()?.let {
            args.putAll(it)
        }

        return args
    }

    protected open fun keepInStack(): Boolean = false
    //endregion


    companion object {
        private const val KEEP_IN_STACK_KEY = "keep_in_stack_key"
        const val ACTIVITY_BUNDLE_EXTRA_KEY = "activity_bundle_extra_key"
        private const val MAX_NUM_OF_BACK_PRESSES = 1
        private const val EXIT_MSG_DELAY_TIME: Long = 2000 // time in milliseconds
    }
}