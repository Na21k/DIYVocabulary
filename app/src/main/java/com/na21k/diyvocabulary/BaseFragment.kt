package com.na21k.diyvocabulary

import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    protected open fun setListeners() {}
    protected open fun observeLiveData() {}

    protected fun switchLoadingMode(isLoading: Boolean, progressBar: ProgressBar) {
        if (isLoading) {
            disableButtons()
            progressBar.visibility = View.VISIBLE
        } else {
            enableButtons()
            progressBar.visibility = View.GONE
        }
    }

    protected open fun disableButtons() {}
    protected open fun enableButtons() {}
}
