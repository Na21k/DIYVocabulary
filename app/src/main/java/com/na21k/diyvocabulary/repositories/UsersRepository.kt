package com.na21k.diyvocabulary.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UsersRepository(application: Application) : BaseRepository(application) {

    val isUserSignedIn get() = mUser != null
    val emailAddress: LiveData<String> get() = _emailAddress

    private val mAuth = Firebase.auth
    private val _emailAddress: MutableLiveData<String> = MutableLiveData(mUser?.email)

    fun signUpWithEmailAndPassword(email: String, password: String, onSuccess: Runnable) {
        _isLoading.postValue(true)
        //If successful, it also signs the user in into the app.
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                onSuccess.run()
            } else {
                _error.postValue(task.exception)
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onSuccess: Runnable) {
        _isLoading.postValue(true)
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                onSuccess.run()
            } else {
                _error.postValue(task.exception)
            }
        }
    }

    fun signOut() {
        mAuth.signOut()
    }

    fun resetPassword(email: String, onSuccess: Runnable) {
        _isLoading.postValue(true)
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                onSuccess.run()
            } else {
                _error.postValue(task.exception)
            }
        }
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

        val credential = EmailAuthProvider.getCredential(user!!.email!!, password)
        user.reauthenticate(credential).addOnCompleteListener { task ->
            _isLoading.postValue(false)

            if (task.isSuccessful) {
                onSuccess.run()
            } else {
                _error.postValue(task.exception)
            }
        }
    }

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
}
