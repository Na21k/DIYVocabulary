package com.na21k.diyvocabulary.ui.home.word

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.na21k.diyvocabulary.WORD_ATTACHED_IMAGES_COUNT_MAX
import com.na21k.diyvocabulary.model.AttachedImageModel
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.model.WordModel
import com.na21k.diyvocabulary.repositories.TagsRepository
import com.na21k.diyvocabulary.repositories.WordImagesRepository
import com.na21k.diyvocabulary.repositories.WordsRepository
import com.na21k.diyvocabulary.ui.shared.BaseViewModel

class WordActivityViewModel(application: Application) : BaseViewModel(application) {

    private val mWordsRepository = WordsRepository(application)
    private val mTagsRepository = TagsRepository(application)
    private val mImagesRepository = WordImagesRepository(application)
    private val mAttachedImagesScheduledForDeletion = mutableListOf<AttachedImageModel>()
    private val mAttachedImagesScheduledForSaving = mutableListOf<AttachedImageModel>()
    private val _imagesUploadTask = MutableLiveData<Task<Void>?>(null)
    val allTagsCache get() = mTagsRepository.allModels.value ?: listOf()
    val attachedImages: LiveData<List<AttachedImageModel>> get() = mImagesRepository.allModels
    val finalAttachedImagesCount: Int
        get() {
            val attached = attachedImages.value?.size ?: 0
            val scheduledForDeletion = mAttachedImagesScheduledForDeletion.size
            val scheduledForSaving = mAttachedImagesScheduledForSaving.size
            return attached - scheduledForDeletion + scheduledForSaving
        }
    override val isLoading: LiveData<Boolean> get() = mImagesRepository.isLoading
    val imagesUploadTask: LiveData<Task<Void>?> get() = _imagesUploadTask

    init {
        //No need to ever remove these observers.
        //Only the ViewModel and Repository (its LiveData) have references to each other,
        //so the JVM's GC can clear them from memory.
        mWordsRepository.error.observeForever { _error.postValue(it) }
        mTagsRepository.error.observeForever { _error.postValue(it) }
        mImagesRepository.error.observeForever { _error.postValue(it) }
    }

    override fun onCleared() {
        mWordsRepository.close()
        mTagsRepository.close()
        mImagesRepository.close()

        super.onCleared()
    }

    fun loadAttachedImages(wordId: String) {
        mImagesRepository.wordId = wordId
        mImagesRepository.resumeObservingData()
    }

    fun attachImage(image: AttachedImageModel): Boolean {
        if (finalAttachedImagesCount >= WORD_ATTACHED_IMAGES_COUNT_MAX) {
            return false
        }

        return mAttachedImagesScheduledForSaving.add(image)
    }

    fun deleteImage(image: AttachedImageModel) {
        mAttachedImagesScheduledForSaving.remove(image)
        mAttachedImagesScheduledForDeletion.add(image)
    }

    fun save(word: WordModel) {
        val docId = mWordsRepository.save(word)
        mImagesRepository.wordId = docId    //in case we're saving a new word

        mAttachedImagesScheduledForDeletion.forEach {
            mImagesRepository.delete(it)
        }

        val imagesUploadTask = mImagesRepository.save(mAttachedImagesScheduledForSaving)
        _imagesUploadTask.postValue(imagesUploadTask)
    }

    fun delete(word: WordModel) {
        mWordsRepository.delete(word)
        attachedImages.value?.forEach {
            mImagesRepository.delete(it)
        }
    }

    fun saveTag(tag: TagModel) = mTagsRepository.save(tag)

    fun cancelRunningImageUploads() {
        mImagesRepository.cancelRunningUploads()
        _imagesUploadTask.postValue(null)
    }
}
