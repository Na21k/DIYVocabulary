package com.na21k.diyvocabulary.repositories

import android.app.Application
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.na21k.diyvocabulary.USER_ID_FIELD_NAME
import com.na21k.diyvocabulary.WORDS_COLLECTION_NAME
import com.na21k.diyvocabulary.WORD_FIELD_NAME
import com.na21k.diyvocabulary.model.WordModel
import java.util.Date

class WordsRepository(application: Application, observeImmediately: Boolean = true) :
    ExposesModelsAsListRepository<WordModel>(application, observeImmediately) {

    override fun observeAll(): ListenerRegistration? {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return null
        }

        _isLoading.postValue(true)

        return mDb.collection(WORDS_COLLECTION_NAME)
            .whereEqualTo(USER_ID_FIELD_NAME, mUser?.uid)
            .orderBy(WORD_FIELD_NAME)
            .addSnapshotListener { querySnapshot, exception ->

                _isLoading.postValue(false)

                if (querySnapshot != null) {
                    val wordDocuments = querySnapshot.documents
                    val words = wordDocuments.map { documentSnapshot ->
                        documentSnapshot.toObject<WordModel>()!!.apply { id = documentSnapshot.id }
                    }
                    _allModels.postValue(words)
                } else {
                    _error.postValue(exception)
                }
            }
    }

    override fun save(model: WordModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        model.userId = mUser?.uid
        model.lastModified = Timestamp(Date())
        val documentId = model.id
        val isNewDocument = documentId == null

        if (isNewDocument) {
            mDb.collection(WORDS_COLLECTION_NAME)
                .add(model.toMap())
        } else {
            mDb.collection(WORDS_COLLECTION_NAME)
                .document(documentId!!)
                .set(model.toMap())
        }
    }

    override fun delete(model: WordModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        val documentId = model.id ?: return

        mDb.collection(WORDS_COLLECTION_NAME)
            .document(documentId)
            .delete()
            .addOnFailureListener {
                _error.postValue(it)
            }
    }
}
