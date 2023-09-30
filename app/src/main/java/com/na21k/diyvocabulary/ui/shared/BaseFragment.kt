package com.na21k.diyvocabulary.ui.shared

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    protected open fun setListeners() {}
    protected open fun observeLiveData() {}
}
