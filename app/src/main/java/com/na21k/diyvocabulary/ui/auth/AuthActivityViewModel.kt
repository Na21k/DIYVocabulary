package com.na21k.diyvocabulary.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivityViewModel : ViewModel() {

    private val mAuth = Firebase.auth
    private val _error =
        MutableLiveData<Exception?>()
    private val _isLoading = MutableLiveData(false)
    val error: LiveData<Exception?>
        get() = _error
    val isLoading: LiveData<Boolean>
        get() = _isLoading

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
