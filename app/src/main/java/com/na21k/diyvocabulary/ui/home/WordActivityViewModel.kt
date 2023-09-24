package com.na21k.diyvocabulary.ui.home

import android.app.Application
import com.na21k.diyvocabulary.BaseViewModel
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.model.WordModel
import com.na21k.diyvocabulary.repositories.TagsRepository
import com.na21k.diyvocabulary.repositories.WordsRepository

class WordActivityViewModel(application: Application) : BaseViewModel(application) {

    private val mWordsRepository = WordsRepository(application)
    private val mTagsRepository = TagsRepository(application)
    val allTagsCache get() = mTagsRepository.allModels.value ?: listOf()

    override fun onCleared() {
        mWordsRepository.close()
        mTagsRepository.close()

        super.onCleared()
    }

    fun save(word: WordModel) {
        mWordsRepository.save(word)
    }

    fun delete(word: WordModel) {
        mWordsRepository.delete(word)
    }

    fun saveTag(tag: TagModel) {
        mTagsRepository.save(tag)
    }
}
