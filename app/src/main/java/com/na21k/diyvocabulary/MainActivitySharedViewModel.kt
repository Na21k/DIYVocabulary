package com.na21k.diyvocabulary

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.model.WordModel

class MainActivitySharedViewModel(application: Application) : BaseViewModel(application) {

    private val mUserId: String?
        get() = mUser?.uid

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


    private var mWordsListenerRegistration: ListenerRegistration? = null
    private var mTagsListenerRegistration: ListenerRegistration? = null

    private var mWords = listOf<WordModel>()
    private var mTagsMap: MutableMap<DocumentReference, TagModel> = mutableMapOf()

    private val _tags = MutableLiveData<List<TagModel>>(listOf())
    private val _wordsWithTags = MutableLiveData<List<WordModel>>(listOf())
    val tags: LiveData<List<TagModel>>
        get() = _tags
    val wordsWithTags: LiveData<List<WordModel>>
        get() = _wordsWithTags
    val isUserSignedIn: Boolean
        get() = mUser != null

    fun startObservingData() {
        mWordsListenerRegistration = observeWords()
        mTagsListenerRegistration = observeTags()
    }

    fun stopObservingData() {
        mWordsListenerRegistration?.remove()
        mTagsListenerRegistration?.remove()
    }

    private fun observeWords(): ListenerRegistration? {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return null
        }

        _isLoadingWords = true

        return mDb.collection(WORDS_COLLECTION_NAME)
            .whereEqualTo(USER_ID_FIELD_NAME, mUserId)
            .orderBy(WORD_FIELD_NAME)
            .addSnapshotListener { querySnapshot, exception ->
                _isLoadingWords = false

                if (querySnapshot != null) {
                    mWords = querySnapshot.documents
                        .map { snapshot ->
                            snapshot.toObject<WordModel>()!!.apply { id = snapshot.id }
                        }
                    setTagsForWords()
                } else {
                    _error.postValue(exception)
                }
            }
    }

    private fun observeTags(): ListenerRegistration? {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return null
        }

        _isLoadingTags = true

        return mDb.collection(TAGS_COLLECTION_NAME)
            .whereEqualTo(USER_ID_FIELD_NAME, mUserId)
            .addSnapshotListener { querySnapshot, exception ->
                _isLoadingTags = false

                if (querySnapshot != null) {
                    val tagDocuments = querySnapshot.documents

                    mTagsMap.clear()
                    mTagsMap.putAll(tagDocuments.map { snapshot ->
                        snapshot.reference to snapshot.toObject<TagModel>()!!
                            .apply { id = snapshot.id }
                    })
                    _tags.postValue(tagDocuments.map { snapshot ->
                        snapshot.toObject<TagModel>()!!.apply { id = snapshot.id }
                    })

                    setTagsForWords()
                } else {
                    _error.postValue(exception)
                }
            }
    }

    private fun setTagsForWords() {
        if (_isLoadingWords || _isLoadingTags) {
            return
        }

        val wordsWithTags = mutableListOf<WordModel>()

        mWords.forEach { word ->
            val wordTagModels = mutableListOf<TagModel>()

            mTagsMap.forEach { (tagRef, tagModel) ->
                val wordTagsReferencesContainsThis =
                    word.tags?.find { wordTagRef -> wordTagRef.id == tagRef.id } != null

                if (wordTagsReferencesContainsThis) {
                    wordTagModels.add(tagModel)
                }
            }

            word.tagModels = wordTagModels
            wordsWithTags.add(word)
        }

        _wordsWithTags.postValue(wordsWithTags)
    }

    fun deleteWord(word: WordModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        val documentId = word.id ?: return

        mDb.collection(WORDS_COLLECTION_NAME)
            .document(documentId)
            .delete()
            .addOnFailureListener {
                _error.postValue(it)
            }
    }

    fun saveTag(tag: TagModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        tag.userId = mUserId
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

    fun deleteTag(tag: TagModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        val documentId = tag.id ?: return

        mDb.collection(TAGS_COLLECTION_NAME)
            .document(documentId)
            .delete()
            .addOnFailureListener {
                _error.postValue(it)
            }
    }
}
