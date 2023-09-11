package com.na21k.diyvocabulary.ui.auth

import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.inputmethod.EditorInfo.IME_ACTION_GO
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.math.MathUtils
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.na21k.diyvocabulary.BaseActivity
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.ActivityAuthBinding

class AuthActivity : BaseActivity() {

    private lateinit var mBinding: ActivityAuthBinding
    private lateinit var mViewModel: AuthActivityViewModel
    private var mIsLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.appBar.appBar)

        mViewModel = ViewModelProvider(this)[AuthActivityViewModel::class.java]

        onBackPressedDispatcher.addCallback(this) {
            //not finishing the activity
        }

        takeCareOfWindowInsets(mBinding.root, true)
        setListeners()
        observeLiveData()
    }

    override fun setListeners() {
        mBinding.signInButton.setOnClickListener { onSignInClick() }
        mBinding.signUpButton.setOnClickListener { onSignUpClick() }
        mBinding.resetPasswordButton.setOnClickListener { onResetPassword() }
        mBinding.loginField.doAfterTextChanged { clearLoginError(); clearUnexpectedError() }
        mBinding.passwordField.doAfterTextChanged { clearPasswordError(); clearUnexpectedError() }
        mBinding.passwordField.setOnEditorActionListener(TextView.OnEditorActionListener
        { _, actionId, _ ->
            if (actionId == IME_ACTION_GO && !mIsLoading) {
                onSignInClick()

                return@OnEditorActionListener true
            }

            return@OnEditorActionListener false
        })
    }

    private fun onSignInClick() {
        val email = mBinding.loginField.text?.toString()
        val password = mBinding.passwordField.text?.toString()

        if (email.isNullOrBlank()) {
            mBinding.loginLayout.error = resources.getString(R.string.validation_required)
            return
        }
        if (password.isNullOrBlank()) {
            mBinding.passwordLayout.error = resources.getString(R.string.validation_required)
            return
        }

        mViewModel.signInWithEmailAndPassword(email, password, onSuccess = { finish() })
    }

    private fun onSignUpClick() {
        val email = mBinding.loginField.text?.toString()
        val password = mBinding.passwordField.text?.toString()

        if (email.isNullOrBlank()) {
            mBinding.loginLayout.error = resources.getString(R.string.validation_required)
            return
        }
        if (password.isNullOrBlank()) {
            mBinding.passwordLayout.error = resources.getString(R.string.validation_required)
            return
        }

        //If successful, it also signs the user in into the app.
        mViewModel.signUpWithEmailAndPassword(email, password, onSuccess = { finish() })
    }

    private fun onResetPassword() {
        val email = mBinding.loginField.text?.toString()

        if (email.isNullOrBlank()) {
            mBinding.loginLayout.error = resources.getString(R.string.validation_required)
            return
        }

        mViewModel.resetPassword(email, onSuccess = {
            AlertDialog.Builder(this)
                .setMessage(R.string.password_reset_email_sent_alert_message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        })
    }

    override fun disableButtons() {
        mBinding.signInButton.isEnabled = false
        mBinding.signUpButton.isEnabled = false
        mBinding.resetPasswordButton.isEnabled = false
    }

    override fun enableButtons() {
        mBinding.signInButton.isEnabled = true
        mBinding.signUpButton.isEnabled = true
        mBinding.resetPasswordButton.isEnabled = true
    }

    private fun clearLoginError() {
        mBinding.loginLayout.error = null
    }

    private fun clearPasswordError() {
        mBinding.passwordLayout.error = null
    }

    private fun clearUnexpectedError() {
        mBinding.unexpectedErrorText.text = null
        mBinding.unexpectedErrorText.visibility = View.GONE
    }

    private fun showUnexpectedError(errorText: String) {
        mBinding.unexpectedErrorText.text = errorText
        mBinding.unexpectedErrorText.visibility = View.VISIBLE
    }

    override fun observeLiveData() {
        mViewModel.error.observe(this) {
            if (it is FirebaseAuthInvalidUserException) {
                mBinding.loginLayout.error = it.message
                mBinding.loginField.requestFocus()
                return@observe
            }
            if (it is FirebaseAuthUserCollisionException) {
                mBinding.loginLayout.error = it.message
                mBinding.loginField.requestFocus()
                return@observe
            }
            if (it is FirebaseAuthWeakPasswordException) {
                mBinding.passwordLayout.error = it.reason
                mBinding.passwordField.requestFocus()
                return@observe
            }
            if (it is FirebaseAuthInvalidCredentialsException &&
                it.message == "The email address is badly formatted."
            //TODO: test with different locales
            ) {
                mBinding.loginLayout.error = it.message
                mBinding.loginField.requestFocus()
                return@observe
            }
            if (it is FirebaseAuthInvalidCredentialsException &&
                it.message == "The password is invalid or the user does not have a password."
            //TODO: test with different locales and use a localized string
            ) {
                mBinding.passwordLayout.error = "The password is invalid."
                mBinding.passwordField.requestFocus()
                return@observe
            }

            val errorMessage = it?.message

            if (errorMessage != null) {
                showUnexpectedError(errorMessage)
            }
        }
        mViewModel.isLoading.observe(this) { isLoading: Boolean ->
            mIsLoading = isLoading
            val progressBar = mBinding.progressBar
            switchLoadingMode(isLoading, progressBar)
        }
    }

    override fun onStart() {
        super.onStart()
        animate()
    }

    private fun animate() {
        val initialDelay = 500L
        animateScale(mBinding.loginLayout, initialDelay)
        animateScale(mBinding.passwordLayout, initialDelay + 50)
        animateScale(mBinding.signInButton, initialDelay + 100)
        animateScale(mBinding.signUpButton, initialDelay + 150)
        animateScale(mBinding.resetPasswordButton, initialDelay + 150)
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

    override fun takeCareOfWindowInsets(rootView: View, countImeInset: Boolean) {
        super.takeCareOfWindowInsets(rootView, countImeInset)

        val callback = getWindowInsetsAnimationCallback()
        ViewCompat.setWindowInsetsAnimationCallback(rootView, callback)
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
        mBinding.unexpectedErrorText.translationY = translationY
        mBinding.progressBar.translationY = translationY
        mBinding.signInButton.translationY = translationY
        mBinding.signUpButton.translationY = translationY
        mBinding.resetPasswordButton.translationY = translationY
    }
}
