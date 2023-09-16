package com.na21k.diyvocabulary.ui.auth

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import com.na21k.diyvocabulary.BaseActivity
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.ActivityProfileBinding
import com.na21k.diyvocabulary.databinding.NewEmailAlertViewBinding
import com.na21k.diyvocabulary.databinding.NewPasswordAlertViewBinding
import com.na21k.diyvocabulary.databinding.ReauthenticationAlertViewBinding
import com.na21k.diyvocabulary.helpers.requestAuth
import com.na21k.diyvocabulary.helpers.showErrorAlertDialog

class ProfileActivity : BaseActivity() {

    private lateinit var mBinding: ActivityProfileBinding
    private lateinit var mViewModel: ProfileActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.appBar.appBar)

        mViewModel = ViewModelProvider(this)[ProfileActivityViewModel::class.java]

        enableUpNavigation(mBinding.appBar.appBar)
        takeCareOfWindowInsets(mBinding.root)
        setListeners()
        observeLiveData()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.reloadUser()
    }

    override fun setListeners() {
        mBinding.signOut.setOnClickListener { onSignOutClick() }
        mBinding.changeEmail.setOnClickListener { onChangeEmailClick() }
        mBinding.changePassword.setOnClickListener { onChangePasswordClick() }
        mBinding.deleteAccount.setOnClickListener { onDeleteAccountClick() }
    }

    private fun onSignOutClick() {
        mViewModel.signOut()
        requestAuth(this)
        finish()
    }

    private fun onChangeEmailClick() {
        val viewBinding = NewEmailAlertViewBinding.inflate(layoutInflater)
        viewBinding.email.requestFocus()

        AlertDialog.Builder(this).setView(viewBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val email = viewBinding.email.text

                if (!email.isNullOrBlank()) {
                    mViewModel.changeEmail(email.toString(), onSuccess = {
                        Snackbar.make(
                            mBinding.root,
                            R.string.email_updated_successfully,
                            Snackbar.LENGTH_LONG
                        ).show()
                    })
                } else {
                    onChangeEmailClick()
                    showErrorAlertDialog(this, R.string.email_cannot_be_empty_alert_message)
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
            .window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun onChangePasswordClick() {
        val viewBinding = NewPasswordAlertViewBinding.inflate(layoutInflater)
        viewBinding.password.requestFocus()

        AlertDialog.Builder(this).setView(viewBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val password = viewBinding.password.text

                if (!password.isNullOrBlank()) {
                    mViewModel.changePassword(password.toString(), onSuccess = {
                        Snackbar.make(
                            mBinding.root,
                            R.string.password_updated_successfully,
                            Snackbar.LENGTH_LONG
                        ).show()
                    })
                } else {
                    onChangePasswordClick()
                    showErrorAlertDialog(this, R.string.password_cannot_be_empty_alert_message)
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
            .window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun onDeleteAccountClick() {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_account_confirmation_alert_message)
            .setIcon(R.drawable.ic_warning_24)
            .setPositiveButton(android.R.string.ok) { _, _ -> deleteAccount() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    private fun deleteAccount() {
        mViewModel.deleteAccount(onSuccess = { requestAuth(this); finish() })
    }

    override fun observeLiveData() {
        mViewModel.emailAddress.observe(this) {
            mBinding.email.text = getString(R.string.greeting_formatted, it)
        }
        mViewModel.error.observe(this) {
            val message = it?.message
            val snack = Snackbar.make(
                mBinding.root,
                message ?: resources.getString(R.string.unexpected_error_occurred),
                Snackbar.LENGTH_INDEFINITE
            )

            if (it is FirebaseNoSignedInUserException || it is FirebaseAuthInvalidUserException) {
                snack.setAction(R.string.sign_in) {
                    requestAuth(this)
                }
            }
            if (it is FirebaseAuthRecentLoginRequiredException) {
                snack.setAction(R.string.sign_in) {
                    showReauthenticationAlertDialog()
                }
            }

            snack.show()
        }
        mViewModel.isLoading.observe(this) { isLoading ->
            val progressBar = mBinding.progressBar
            switchLoadingMode(isLoading, progressBar)
        }
    }

    override fun disableButtons() {
        mBinding.signOut.isEnabled = false
        mBinding.changeEmail.isEnabled = false
        mBinding.changePassword.isEnabled = false
        mBinding.deleteAccount.isEnabled = false
    }

    override fun enableButtons() {
        mBinding.signOut.isEnabled = true
        mBinding.changeEmail.isEnabled = true
        mBinding.changePassword.isEnabled = true
        mBinding.deleteAccount.isEnabled = true
    }

    private fun showReauthenticationAlertDialog() {
        val viewBinding = ReauthenticationAlertViewBinding.inflate(layoutInflater)
        viewBinding.password.requestFocus()

        AlertDialog.Builder(this).setView(viewBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val password = viewBinding.password.text

                if (!password.isNullOrBlank()) {
                    mViewModel.reauthenticate(password.toString(), onSuccess = {
                        Snackbar.make(
                            mBinding.root,
                            R.string.reauthentication_successful,
                            Snackbar.LENGTH_LONG
                        ).show()
                    })
                } else {
                    showReauthenticationAlertDialog()
                    showErrorAlertDialog(this, R.string.password_cannot_be_empty_alert_message)
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
            .window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}
