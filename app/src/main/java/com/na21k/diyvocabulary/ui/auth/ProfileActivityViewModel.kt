package com.na21k.diyvocabulary.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import com.na21k.diyvocabulary.BaseViewModel
import com.na21k.diyvocabulary.repositories.UsersRepository

class ProfileActivityViewModel(application: Application) : BaseViewModel(application) {

    val emailAddress: LiveData<String> get() = mUsersRepository.emailAddress
    override val error: LiveData<Exception?> get() = mUsersRepository.error
    override val isLoading: LiveData<Boolean> get() = mUsersRepository.isLoading
    private val mUsersRepository = UsersRepository(application)

    fun reloadUser() {
        mUsersRepository.reloadUser()
    }

    fun signOut() {
        mUsersRepository.signOut()
    }

    fun changeEmail(newEmail: String, onSuccess: Runnable) {
        mUsersRepository.changeEmail(newEmail, onSuccess)
    }

    fun changePassword(newPassword: String, onSuccess: Runnable) {
        mUsersRepository.changePassword(newPassword, onSuccess)
    }

    fun deleteAccount(onSuccess: Runnable) {
        mUsersRepository.deleteAccount(onSuccess)
    }

    fun reauthenticate(password: String, onSuccess: Runnable) {
        mUsersRepository.reauthenticate(password, onSuccess)
    }
}
