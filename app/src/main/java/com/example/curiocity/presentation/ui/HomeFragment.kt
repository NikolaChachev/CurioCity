package com.example.curiocity.presentation.ui

import android.view.View
import com.example.curiocity.R
import com.example.curiocity.databinding.FragmentHomeBinding
import com.example.curiocity.presentation.architecture.fragment.CurioFragment
import com.example.curiocity.presentation.architecture.vm.CurioViewModel

class HomeFragment : CurioFragment<FragmentHomeBinding, CurioViewModel>() {

    override fun onPrepareLayout(layoutView: View?) {
        

    }

    override fun getViewModelResId(): Int? = null

    override fun getLayoutResId(): Int = R.layout.fragment_home

    override fun getViewModelClass(): Class<CurioViewModel> = CurioViewModel::class.java
}