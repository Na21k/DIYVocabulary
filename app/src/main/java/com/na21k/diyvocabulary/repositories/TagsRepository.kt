package com.na21k.diyvocabulary.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.na21k.diyvocabulary.TAGS_COLLECTION_NAME
import com.na21k.diyvocabulary.TITLE_FIELD_NAME
import com.na21k.diyvocabulary.USER_ID_FIELD_NAME
import com.na21k.diyvocabulary.model.TagModel

class TagsRepository(application: Application, observeImmediately: Boolean = true) :
    ExposesModelsAsListRepository<TagModel>(application, observeImmediately) {

    val referencesToTagsMap: LiveData<Map<DocumentReference, TagModel>>
        get() = _referencesToTagsMap
    private val _referencesToTagsMap = MutableLiveData<Map<DocumentReference, TagModel>>(
        mutableMapOf()
    )

    override fun observeAll(): ListenerRegistration? {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return null
        }

        _isLoading.postValue(true)

        return mDb.collection(TAGS_COLLECTION_NAME)
            .whereEqualTo(USER_ID_FIELD_NAME, mUser?.uid)
            .orderBy(TITLE_FIELD_NAME)
            .addSnapshotListener { querySnapshot, exception ->

                _isLoading.postValue(false)

                if (querySnapshot != null) {
                    val tagDocuments = querySnapshot.documents

                    val tags = tagDocuments.map { documentSnapshot ->
                        documentSnapshot.toObject<TagModel>()!!.also { it.id = documentSnapshot.id }
                    }
                    _allModels.postValue(tags)

                    val referencesToTagsMap = mutableMapOf<DocumentReference, TagModel>()
                    referencesToTagsMap.putAll(tagDocuments.map { snapshot ->
                        snapshot.reference to snapshot.toObject<TagModel>()!!
                            .apply { id = snapshot.id }
                    })
                    _referencesToTagsMap.postValue(referencesToTagsMap)
                } else {
                    _error.postValue(exception)
                }
            }
    }

    override fun save(model: TagModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        model.userId = user!!.uid
        val documentId = model.id
        val isNewDocument = documentId == null

        if (isNewDocument) {
            mDb.collection(TAGS_COLLECTION_NAME)
                .add(model.toMap())
                .addOnFailureListener { _error.postValue(it) }
        } else {
            mDb.collection(TAGS_COLLECTION_NAME)
                .document(documentId!!)
                .set(model.toMap())
                .addOnFailureListener { _error.postValue(it) }
        }
    }

    override fun delete(model: TagModel) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        val documentId = model.id ?: return

        mDb.collection(TAGS_COLLECTION_NAME)
            .document(documentId)
            .delete()
            .addOnFailureListener {
                _error.postValue(it)
            }
    }
}
