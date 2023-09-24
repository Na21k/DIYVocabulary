package com.na21k.diyvocabulary.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import com.na21k.diyvocabulary.BaseViewModel
import com.na21k.diyvocabulary.repositories.UsersRepository

class AuthActivityViewModel(application: Application) : BaseViewModel(application) {

    override val error: LiveData<Exception?> get() = mUsersRepository.error
    override val isLoading: LiveData<Boolean> get() = mUsersRepository.isLoading
    private val mUsersRepository = UsersRepository(application)

    fun signUpWithEmailAndPassword(email: String, password: String, onSuccess: Runnable) {
        mUsersRepository.signUpWithEmailAndPassword(email, password, onSuccess)
    }

    fun signInWithEmailAndPassword(email: String, password: String, onSuccess: Runnable) {
        mUsersRepository.signInWithEmailAndPassword(email, password, onSuccess)
    }

    fun resetPassword(email: String, onSuccess: Runnable) {
        mUsersRepository.resetPassword(email, onSuccess)
    }
}
