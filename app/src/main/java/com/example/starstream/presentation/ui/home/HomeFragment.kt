package com.example.starstream.presentation.ui.home

import android.os.Bundle
import android.view.View
import com.example.starstream.R
import com.example.starstream.databinding.FragmentHomeBinding
import com.example.starstream.presentation.adapter.FragmentAdapter
import com.example.starstream.presentation.ui.base.BaseFragment
import com.example.starstream.util.LifecycleViewPager
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override val defineBindingVariables: ((FragmentHomeBinding) -> Unit)?
        get() = null

    override var mediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = FragmentAdapter(this)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleViewPager(binding.viewPager))

        val tabTitles = listOf(getString(R.string.tab_title_1)) // Titles for more tabs
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }

        mediator?.attach()
    }

    override fun onDestroyView() {
        mediator?.detach()
        super.onDestroyView()
    }
}
