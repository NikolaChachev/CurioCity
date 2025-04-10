package com.example.curiocity.presentation.architecture.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.curiocity.presentation.architecture.activity.CurioActivity
import com.example.curiocity.presentation.architecture.vm.CurioViewModel
import kotlin.reflect.KClass

abstract class CurioFragment<B : ViewDataBinding, VM : CurioViewModel> : Fragment() {

    protected lateinit var binding: B
    protected val viewModel by lazy { ViewModelProvider(this)[getViewModelClass()] }

    //region base lifecycle methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // abstract fragments should only be used through pre-created abstract activity, due to dependency
        if (activity !is CurioActivity<*, *>) {
            throw IllegalStateException("Activity has to inherit AbstractActivity!")
        }
        val layoutId = getLayoutResId()
        val bindResId = getViewModelResId()

        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        val view = binding.root
        bindResId?.let {
            binding.setVariable(bindResId, viewModel)
        }

        onPrepareLayout(view)
        return view
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    //endregion

    //region Protected methods
    protected open fun hideKeyboardFrom(view: View) {
        val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Callback after fragment's view is ready.
     *
     * @param layoutView - the layout view which is inflated
     */
    protected open fun onPrepareLayout(layoutView: View?) {
        //do nothing
    }

    //endregion

    //region public methods

    fun createShortToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun createShortToast(@StringRes stringRes: Int) {
        Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
    }

    //endregion

    //region navigation methods

    fun <T : CurioFragment<*, *>> navigateToView(clazz: KClass<T>, args: Bundle? = null) {
        activity.let {
            if (it is CurioActivity<*, *>) {
                viewModel.postNavigationArgs()?.let { bundle ->
                    args?.putAll(bundle)
                }
                it.openView(clazz, args)
            }
        }
    }

    fun <T : CurioActivity<*, *>> navigateToActivity(
        clazz: KClass<T>,
        args: Bundle? = null
    ) {
        activity.let {
            if (it is CurioActivity<*, *>) {
                it.openActivity(clazz, args)
            }
        }
    }

    fun navigateBack() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    /**
     * In case we need more complex UI behaviour, like updating when a livedata changes
     * we can add a variable in the xml layout for the viewmodel resource that that layout
     * will use in order to track for such updates.
     * Might be completely unnecessary :)
     */
    abstract fun getViewModelResId(): Int?

    abstract fun getLayoutResId(): Int

    abstract fun getViewModelClass(): Class<VM>

    //endregion

}