package com.na21k.diyvocabulary.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.na21k.diyvocabulary.EXPLANATION_FIELD_NAME
import com.na21k.diyvocabulary.LAST_MODIFIED_FIELD_NAME
import com.na21k.diyvocabulary.TAGS_FIELD_NAME
import com.na21k.diyvocabulary.TRANSCRIPTION_FIELD_NAME
import com.na21k.diyvocabulary.TRANSLATION_FIELD_NAME
import com.na21k.diyvocabulary.USAGE_EXAMPLE_FIELD_NAME
import com.na21k.diyvocabulary.WORD_FIELD_NAME

class WordModel : UserOwnedModel() {

    var word: String? = null
    var transcription: String? = null
    var translation: String? = null
    var explanation: String? = null
    var usageExample: String? = null
    var lastModified: Timestamp? = null
    var tagModels: List<TagModel>? = null
    var tags: List<DocumentReference>? = null

    fun removeTag(tagModel: TagModel) {
        val newTagModels = mutableListOf<TagModel>()
        val newTags = mutableListOf<DocumentReference>()
        tagModels?.let { newTagModels.addAll(it) }
        tags?.let { newTags.addAll(it) }

        newTagModels.remove(tagModel)
        newTags.removeIf { it.id == tagModel.id }

        tagModels = newTagModels
        tags = newTags
    }

    override fun toMap(): Map<String, Any?> {
        val res = mutableMapOf<String, Any?>()
        res.putAll(super.toMap())
        res.putAll(
            mapOf(
                WORD_FIELD_NAME to word,
                TRANSCRIPTION_FIELD_NAME to transcription,
                TRANSLATION_FIELD_NAME to translation,
                EXPLANATION_FIELD_NAME to explanation,
                USAGE_EXAMPLE_FIELD_NAME to usageExample,
                LAST_MODIFIED_FIELD_NAME to lastModified,
                TAGS_FIELD_NAME to tags
            )
        )

        return res
    }

    private fun writeObject(outObj: java.io.ObjectOutputStream) {
        outObj.writeObject(id)
        outObj.writeObject(userId)
        outObj.writeObject(word)
        outObj.writeObject(transcription)
        outObj.writeObject(translation)
        outObj.writeObject(explanation)
        outObj.writeObject(usageExample)
        outObj.writeObject(lastModified?.seconds)
        outObj.writeObject(lastModified?.nanoseconds)
        outObj.writeObject(tagModels)
        outObj.writeObject(tags?.map {
            it.path
        })
    }

    private fun readObject(inObj: java.io.ObjectInputStream) {
        id = inObj.readObject() as String?
        userId = inObj.readObject() as String?
        word = inObj.readObject() as String?
        transcription = inObj.readObject() as String?
        translation = inObj.readObject() as String?
        explanation = inObj.readObject() as String?
        usageExample = inObj.readObject() as String?

        val lastModifiedSeconds = inObj.readObject() as Long?
        val lastModifiedNanoseconds = inObj.readObject() as Int?

        lastModified = Timestamp(lastModifiedSeconds ?: 0, lastModifiedNanoseconds ?: 0)
        tagModels = inObj.readObject() as List<TagModel>?
        tags = (inObj.readObject() as List<String>?)?.map {
            Firebase.firestore.document(it)
        }
    }
}
