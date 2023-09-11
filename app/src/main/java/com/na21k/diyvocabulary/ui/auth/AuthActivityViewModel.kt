package com.na21k.diyvocabulary.ui.auth

import android.app.Application
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.na21k.diyvocabulary.BaseViewModel

class AuthActivityViewModel(application: Application) : BaseViewModel(application) {

    private val mAuth = Firebase.auth

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
}
