package com.na21k.diyvocabulary.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration

abstract class ExposesModelsAsListRepository<T>(
    application: Application,
    observeImmediately: Boolean
) :
    BaseRepository(application),
    AutoCloseable {

    val allModels: LiveData<List<T>> get() = _allModels
    protected val _allModels = MutableLiveData<List<T>>(listOf())

    private var mListenerRegistration: ListenerRegistration? = null

    init {
        if (observeImmediately) {
            resumeObservingData()
        }
    }

    override fun close() {
        pauseObservingData()
    }

    fun resumeObservingData() {
        mListenerRegistration = observeAll()
    }

    fun pauseObservingData() {
        mListenerRegistration?.remove()
    }

    protected abstract fun observeAll(): ListenerRegistration?
    abstract fun save(model: T)
    abstract fun delete(model: T)
}
