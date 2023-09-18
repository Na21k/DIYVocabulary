package com.na21k.diyvocabulary.ui.home

import android.app.Application
import com.google.firebase.Timestamp
import com.na21k.diyvocabulary.BaseViewModel
import com.na21k.diyvocabulary.WORDS_COLLECTION_NAME
import com.na21k.diyvocabulary.model.WordModel
import java.util.Date

class WordActivityViewModel(application: Application) : BaseViewModel(application) {

    fun save(word: WordModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        word.userId = mUser?.uid
        word.lastModified = Timestamp(Date())
        val documentId = word.id
        val isNewDocument = documentId == null

        if (isNewDocument) {
            mDb.collection(WORDS_COLLECTION_NAME)
                .add(word.toMap())
        } else {
            mDb.collection(WORDS_COLLECTION_NAME)
                .document(documentId!!)
                .set(word.toMap())
        }
    }

    fun delete(word: WordModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        val documentId = word.id ?: return

        mDb.collection(WORDS_COLLECTION_NAME)
            .document(documentId)
            .delete()
    }
}
