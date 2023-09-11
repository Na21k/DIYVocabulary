package com.na21k.diyvocabulary.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import com.google.firebase.ktx.Firebase
import com.na21k.diyvocabulary.BaseViewModel
import com.na21k.diyvocabulary.R

class ProfileViewModel(application: Application) : BaseViewModel(application) {

    private val mUser: FirebaseUser?
        get() = Firebase.auth.currentUser
    private val _emailAddress: MutableLiveData<String> = MutableLiveData(mUser?.email)
    val emailAddress: LiveData<String>
        get() = _emailAddress

    private fun ensureSignedIn(user: FirebaseUser?): Boolean {
        if (user == null) {
            _error.postValue(
                FirebaseNoSignedInUserException(
                    getApplication<Application>().getString(R.string.not_authenticated)
                )
            )
            return false
        }

        return true
    }

    fun reloadUser() {
        mUser?.reload()
        _emailAddress.postValue(mUser?.email)
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

    fun changeEmail(newEmail: String, onSuccess: Runnable) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        _isLoading.postValue(true)

        // Unlike updateEmail(), verifyBeforeUpdateEmail() requires the user to follow
        // a verification link before Identity Platform updates their email address
        user!!./*verifyBeforeUpdateEmail*/updateEmail(newEmail).addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                _emailAddress.postValue(newEmail)
                onSuccess.run()
            } else {
                _error.postValue(task.exception)
            }
        }
    }

    fun changePassword(newPassword: String, onSuccess: Runnable) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        _isLoading.postValue(true)

        user!!.updatePassword(newPassword).addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                onSuccess.run()
            } else {
                _error.postValue(task.exception)
            }
        }
    }

    fun deleteAccount(onSuccess: Runnable) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        _isLoading.postValue(true)

        user!!.delete().addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                onSuccess.run()
            } else {
                _error.postValue(task.exception)
            }
        }
    }

    fun reauthenticate(password: String, onSuccess: Runnable) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        _isLoading.postValue(true)

        val credential = EmailAuthProvider.getCredential(mUser!!.email!!, password)
        mUser!!.reauthenticate(credential).addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                onSuccess.run()
            } else {
                _error.postValue(task.exception)
            }
        }
    }
}
