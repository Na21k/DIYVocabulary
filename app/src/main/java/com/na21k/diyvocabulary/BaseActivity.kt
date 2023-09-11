package com.na21k.diyvocabulary

import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

open class BaseActivity : AppCompatActivity() {

    protected fun enableUpNavigation(appBar: Toolbar) {
        appBar.setNavigationIcon(R.drawable.ic_arrow_back_24)
        appBar.setNavigationContentDescription(R.string.navigate_up_content_description)
        appBar.setNavigationOnClickListener { finish() }
    }

    protected open fun takeCareOfWindowInsets(rootView: View, countImeInset: Boolean = false) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.TRANSPARENT

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            var insetsTypeMask = WindowInsetsCompat.Type.systemBars()

            if (countImeInset) {
                insetsTypeMask = insetsTypeMask or WindowInsetsCompat.Type.ime()
            }

            val i = insets.getInsets(insetsTypeMask)

            rootView.setPadding(i.left, i.top, i.right, i.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

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
