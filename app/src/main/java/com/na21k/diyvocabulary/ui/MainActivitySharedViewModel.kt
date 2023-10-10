package com.na21k.diyvocabulary.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.firestore.DocumentReference
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.model.WordModel
import com.na21k.diyvocabulary.repositories.TagsRepository
import com.na21k.diyvocabulary.repositories.UsersRepository
import com.na21k.diyvocabulary.repositories.WordImagesRepository
import com.na21k.diyvocabulary.repositories.WordsRepository
import com.na21k.diyvocabulary.ui.shared.BaseViewModel

class MainActivitySharedViewModel(application: Application) : BaseViewModel(application) {

    val tags: LiveData<List<TagModel>> get() = mTagsRepository.allModels
    val wordsWithTags: LiveData<List<WordModel>> get() = _wordsWithTags
    val isUserSignedIn get() = UsersRepository(getApplication()).isUserSignedIn

    private val mTagsRepository = TagsRepository(application, false)
    private val mWordsRepository = WordsRepository(application, false)
    private val mImagesRepository = WordImagesRepository(application)

    private val mWordsRepositoryErrorObserver = Observer<Exception?> { _error.postValue(it) }
    private val mTagsRepositoryErrorObserver = Observer<Exception?> { _error.postValue(it) }
    private val mImagesRepositoryErrorObserver = Observer<Exception?> { _error.postValue(it) }

    private val mIsLoadingWordsObserver = Observer<Boolean> { _isLoadingWords = it }
    private val mIsLoadingTagsObserver = Observer<Boolean> { _isLoadingTags = it }

    private val mWordsObserver = Observer<List<WordModel>> { mWords = it }
    private val mReferencesToTagsMapObserver =
        Observer<Map<DocumentReference, TagModel>> { mReferencesToTagsMap = it }

    private var _isLoadingWords = false
        set(value) {
            field = value
            _isLoading.postValue(value || _isLoadingTags)
        }
    private var _isLoadingTags = false
        set(value) {
            field = value
            _isLoading.postValue(value || _isLoadingWords)
        }

    private var mWords = listOf<WordModel>()
        set(value) {
            field = value
            setTagsForWords()
        }
    private var mReferencesToTagsMap: Map<DocumentReference, TagModel> = mutableMapOf()
        set(value) {
            field = value
            setTagsForWords()
        }

    private val _wordsWithTags = MutableLiveData<List<WordModel>>(listOf())

    override fun onCleared() {
        mWordsRepository.close()
        mTagsRepository.close()
        mImagesRepository.close()
        //No need to ensure LiveData isn't observed anymore (stopObservingData() has been called).
        //Only the ViewModel and Repository (its LiveData) have references to each other,
        //so the JVM's GC can clear them from memory.

        super.onCleared()
    }

    override fun consumeError() {
        mWordsRepository.consumeError()
        mTagsRepository.consumeError()
        mImagesRepository.consumeError()
        super.consumeError()
    }

    fun startObservingData() {
        observeWordsRepo()
        observeTagsRepo()
        observeImagesRepo()
        mWordsRepository.resumeObservingData()
        mTagsRepository.resumeObservingData()
    }

    fun stopObservingData() {
        removeObserversWordsRepo()
        removeObserversTagsRepo()
        removeObserversImagesRepo()
        mWordsRepository.pauseObservingData()
        mTagsRepository.pauseObservingData()
    }

    private fun observeWordsRepo() {
        mWordsRepository.error.observeForever(mWordsRepositoryErrorObserver)
        mWordsRepository.isLoading.observeForever(mIsLoadingWordsObserver)
        mWordsRepository.allModels.observeForever(mWordsObserver)
    }

    private fun removeObserversWordsRepo() {
        mWordsRepository.error.removeObserver(mWordsRepositoryErrorObserver)
        mWordsRepository.isLoading.removeObserver(mIsLoadingWordsObserver)
        mWordsRepository.allModels.removeObserver(mWordsObserver)
    }

    private fun observeTagsRepo() {
        mTagsRepository.error.observeForever(mTagsRepositoryErrorObserver)
        mTagsRepository.isLoading.observeForever(mIsLoadingTagsObserver)
        mTagsRepository.referencesToTagsMap.observeForever(mReferencesToTagsMapObserver)
    }

    private fun removeObserversTagsRepo() {
        mTagsRepository.error.removeObserver(mTagsRepositoryErrorObserver)
        mTagsRepository.isLoading.removeObserver(mIsLoadingTagsObserver)
        mTagsRepository.referencesToTagsMap.removeObserver(mReferencesToTagsMapObserver)
    }

    private fun observeImagesRepo() {
        mImagesRepository.error.observeForever(mImagesRepositoryErrorObserver)
    }

    private fun removeObserversImagesRepo() {
        mImagesRepository.error.removeObserver(mImagesRepositoryErrorObserver)
    }

    private fun setTagsForWords() {
        if (_isLoadingWords || _isLoadingTags) {
            return
        }

        val wordsWithTags = mutableListOf<WordModel>()

        mWords.forEach { word ->
            word.setTagModels(mReferencesToTagsMap)
            wordsWithTags.add(word)
        }

        _wordsWithTags.postValue(wordsWithTags)
    }

    fun deleteWord(word: WordModel) {
        mWordsRepository.delete(word)
        word.id?.let { mImagesRepository.delete(it) }
    }

    fun saveTag(tag: TagModel) {
        mTagsRepository.save(tag)
    }

    fun deleteTag(tag: TagModel) {
        mTagsRepository.delete(tag)
    }
}
