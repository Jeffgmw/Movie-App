package com.example.starstream.presentation.ui.favoritemovies

import android.os.Bundle
import android.view.View
import com.example.starstream.R
import com.example.starstream.databinding.FragmentFavoritesBinding
import com.example.starstream.presentation.adapter.FragmentAdapter
import com.example.starstream.presentation.ui.base.BaseFragment
import com.example.starstream.util.LifecycleViewPager
import com.google.android.material.tabs.TabLayoutMediator

class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>(R.layout.fragment_favorites) {

    override val defineBindingVariables: ((FragmentFavoritesBinding) -> Unit)?
        get() = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = FragmentAdapter(this)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleViewPager(binding.viewPager))

        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabTitles = listOf(getString(R.string.tab_title_1))
            tab.text = tabTitles[position]
        }

        mediator?.attach()
    }
}