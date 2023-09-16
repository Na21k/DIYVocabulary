package com.na21k.diyvocabulary.ui.tags.tagDialog

import android.app.Application
import com.google.firebase.firestore.ktx.toObject
import com.na21k.diyvocabulary.BaseViewModel
import com.na21k.diyvocabulary.TAGS_COLLECTION_NAME
import com.na21k.diyvocabulary.model.TagModel

class TagDialogFragmentViewModel(application: Application) : BaseViewModel(application) {

    fun fetchTag(tagDocumentId: String, onFetchListener: OnFetchListener) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        mDb.collection(TAGS_COLLECTION_NAME)
            .document(tagDocumentId)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onFetchListener.onFetched(
                        task.result.toObject<TagModel>()?.apply { id = tagDocumentId })
                } else {
                    onFetchListener.onError(task.exception)
                }
            }
    }

    interface OnFetchListener {
        fun onFetched(tag: TagModel?)
        fun onError(exception: Exception?)
    }
}
