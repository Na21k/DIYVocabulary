package com.na21k.diyvocabulary.ui.home

import android.app.Application
import com.google.firebase.firestore.ktx.toObject
import com.na21k.diyvocabulary.BaseViewModel
import com.na21k.diyvocabulary.WORDS_COLLECTION_NAME
import com.na21k.diyvocabulary.model.WordModel

class WordActivityViewModel(application: Application) : BaseViewModel(application) {

    fun fetchWord(wordDocumentId: String, onFetchListener: OnFetchListener) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        mDb.collection(WORDS_COLLECTION_NAME)
            .document(wordDocumentId)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onFetchListener.onFetched(
                        task.result.toObject<WordModel>()?.apply { id = wordDocumentId })
                } else {
                    onFetchListener.onError(task.exception)
                }
            }
    }

    interface OnFetchListener {
        fun onFetched(word: WordModel?)
        fun onError(exception: Exception?)
    }
}
