package com.na21k.diyvocabulary.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.na21k.diyvocabulary.BaseViewModel

class ProfileActivityViewModel(application: Application) : BaseViewModel(application) {

    private val _emailAddress: MutableLiveData<String> = MutableLiveData(mUser?.email)
    val emailAddress: LiveData<String>
        get() = _emailAddress

    fun reloadUser() {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        _isLoading.postValue(true)

        user!!.reload().addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                _emailAddress.postValue(user.email)
            } else {
                _error.postValue(task.exception)
            }
        }
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
