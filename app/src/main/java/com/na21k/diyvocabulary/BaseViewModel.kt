package com.na21k.diyvocabulary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val _error = MutableLiveData<Exception?>()
    protected val _isLoading = MutableLiveData(false)
    open val error: LiveData<Exception?>
        get() = _error
    open val isLoading: LiveData<Boolean>
        get() = _isLoading

    open fun consumeError() {
        _error.postValue(null)
    }
}
