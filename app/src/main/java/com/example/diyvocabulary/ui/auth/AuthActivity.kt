package com.example.diyvocabulary.ui.auth

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import com.example.diyvocabulary.databinding.ActivityAuthBinding
import com.google.android.material.math.MathUtils

class AuthActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.appBar.appBar)

        takeCareOfWindowInsets()
    }

    override fun onStart() {
        super.onStart()
        animate()
    }

    private fun animate() {
        val initialDelay = 500L
        animateScale(mBinding.loginLayout, initialDelay)
        animateScale(mBinding.passwordLayout, initialDelay + 50)
        animateScale(mBinding.logInButton, initialDelay + 100)
        animateScale(mBinding.signUpButton, initialDelay + 150)
    }

    private fun animateScale(view: View, delayMillis: Long): ViewPropertyAnimator {
        return view.animate()
            .setStartDelay(delayMillis)
            .setDuration(200)
            .scaleX(1.15f).scaleY(1.15f)
            .withEndAction {
                view.animate()
                    .setStartDelay(0)
                    .setDuration(100)
                    .scaleX(1f).scaleY(1f)
            }
    }

    private fun takeCareOfWindowInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.TRANSPARENT

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { _, insets ->
            val i = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.ime()
            )

            mBinding.root.setPadding(i.left, i.top, i.right, i.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val callback = getWindowInsetsAnimationCallback()
        ViewCompat.setWindowInsetsAnimationCallback(mBinding.root, callback)
    }

    private fun getWindowInsetsAnimationCallback(): WindowInsetsAnimationCompat.Callback {
        return object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
            var positionStart = 0f
            var positionEnd = 0f

            override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                positionStart = mBinding.loginLayout.top.toFloat()
            }

            override fun onStart(
                animation: WindowInsetsAnimationCompat,
                bounds: WindowInsetsAnimationCompat.BoundsCompat
            ): WindowInsetsAnimationCompat.BoundsCompat {
                //OnApplyWindowInsetsListener has already been called

                positionEnd = mBinding.loginLayout.top.toFloat()
                moveAllExceptAppBar(positionStart)

                return bounds
            }

            override fun onProgress(
                insets: WindowInsetsCompat,
                runningAnimations: MutableList<WindowInsetsAnimationCompat>
            ): WindowInsetsCompat {
                val imeAnimation = runningAnimations.find {
                    it.typeMask and WindowInsetsCompat.Type.ime() != 0
                } ?: return insets

                // Offset the view based on the interpolated fraction of the IME animation.
                val translation = MathUtils.lerp(
                    positionStart - positionEnd,
                    0f,
                    imeAnimation.interpolatedFraction   //interpolatedFraction 0 -> 1
                )
                moveAllExceptAppBar(translation)

                return insets
            }
        }
    }

    private fun moveAllExceptAppBar(translationY: Float) {
        mBinding.loginLayout.translationY = translationY
        mBinding.passwordLayout.translationY = translationY
        mBinding.logInButton.translationY = translationY
        mBinding.signUpButton.translationY = translationY
    }
}
