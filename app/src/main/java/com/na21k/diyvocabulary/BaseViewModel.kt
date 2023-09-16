package com.na21k.diyvocabulary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import com.google.firebase.ktx.Firebase

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val _error = MutableLiveData<Exception?>()
    protected val _isLoading = MutableLiveData(false)
    val error: LiveData<Exception?>
        get() = _error
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    protected val mUser: FirebaseUser?
        get() = Firebase.auth.currentUser
    protected val mDb: FirebaseFirestore
        get() = Firebase.firestore

    protected fun ensureSignedIn(user: FirebaseUser?): Boolean {
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

    fun consumeError() {
        _error.postValue(null)
    }
}
