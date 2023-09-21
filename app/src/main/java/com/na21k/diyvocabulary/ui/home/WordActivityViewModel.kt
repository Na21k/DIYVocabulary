package com.na21k.diyvocabulary.ui.home

import android.app.Application
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.na21k.diyvocabulary.BaseViewModel
import com.na21k.diyvocabulary.TAGS_COLLECTION_NAME
import com.na21k.diyvocabulary.TITLE_FIELD_NAME
import com.na21k.diyvocabulary.USER_ID_FIELD_NAME
import com.na21k.diyvocabulary.WORDS_COLLECTION_NAME
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.model.WordModel
import java.util.Date

class WordActivityViewModel(application: Application) : BaseViewModel(application) {

    private var mAllTagsListenerRegistration: ListenerRegistration?
    private var _allTagsCache: List<TagModel> = listOf()
    val allTagsCache get() = _allTagsCache

    init {
        mAllTagsListenerRegistration = observeAllTags()
    }

    override fun onCleared() {
        mAllTagsListenerRegistration?.remove()
        super.onCleared()
    }

    private fun observeAllTags(): ListenerRegistration? {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return null
        }

        return mDb.collection(TAGS_COLLECTION_NAME)
            .whereEqualTo(USER_ID_FIELD_NAME, mUser?.uid)
            .orderBy(TITLE_FIELD_NAME)
            .addSnapshotListener { querySnapshot, exception ->

                if (querySnapshot != null) {
                    val tagDocuments = querySnapshot.documents
                    _allTagsCache = tagDocuments.map { documentSnapshot ->
                        documentSnapshot.toObject<TagModel>()!!.also { it.id = documentSnapshot.id }
                    }
                } else {
                    _error.postValue(exception)
                }
            }
    }

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

    fun saveTag(tag: TagModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        tag.userId = user!!.uid
        val documentId = tag.id
        val isNewDocument = documentId == null

        if (isNewDocument) {
            mDb.collection(TAGS_COLLECTION_NAME)
                .add(tag.toMap())
                .addOnFailureListener { _error.postValue(it) }
        } else {
            mDb.collection(TAGS_COLLECTION_NAME)
                .document(documentId!!)
                .set(tag.toMap())
                .addOnFailureListener { _error.postValue(it) }
        }
    }
}
