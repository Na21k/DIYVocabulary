package com.na21k.diyvocabulary.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
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
}
