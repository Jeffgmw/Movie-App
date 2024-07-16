package com.example.starstream.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.starstream.R
import com.example.starstream.presentation.ui.seeall.SeeAllFragment
import com.example.starstream.util.Constants
import com.example.starstream.util.isDarkColor
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

abstract class BaseFragment<B : ViewDataBinding>(@LayoutRes private val layoutId: Int) : Fragment() {

    private var _binding: B? = null
    protected open val binding get() = _binding!!

    protected abstract val defineBindingVariables: ((B) -> Unit)?

    protected open var mediator: TabLayoutMediator? = null

    private var snackbar: Snackbar? = null

    protected val fragmentId by lazy { arguments?.getInt(Constants.DETAIL_ID, 0) ?: 0 }

    val backgroundColor by lazy { arguments?.getInt(Constants.BACKGROUND_COLOR, 0) ?: 0 }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate<B>(inflater, layoutId, container, false).apply { defineBindingVariables?.invoke(this) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    protected open fun initBinding() {
        activity?.let { act ->
            when (this) {
                is SeeAllFragment -> {
                    if (backgroundColor != 0) {
                        act.setTheme(if (backgroundColor.isDarkColor()) R.style.SeeAllDarkTheme else R.style.SeeAllLightTheme)
                        act.window.statusBarColor = backgroundColor
                    }
                }
                else -> {
                    act.setTheme(if (backgroundColor.isDarkColor()) R.style.DetailDarkTheme else R.style.DetailLightTheme)
                }
            }
        }
        binding.apply { defineBindingVariables?.invoke(this) }
    }

    protected fun collectFlows(collectors: List<suspend () -> Unit>) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectors.forEach { collector ->
                    launch { collector.invoke() }
                }
            }
        }
    }

    protected fun showSnackbar(message: String, indefinite: Boolean = true, actionText: String? = null, anchor: Boolean = false, action: (() -> Unit)? = null) {
        val view = activity?.window?.decorView?.rootView
        val length = if (indefinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG
        val snackbar = view?.let { Snackbar.make(it, message, length) }

        if (action != null) snackbar?.setAction(actionText) { action() }
        if (anchor) snackbar?.setAnchorView(R.id.bottom_nav_bar)

        this.snackbar = snackbar
        this.snackbar?.show()
    }

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mediator?.detach()
        mediator = null
        _binding = null
    }
}
