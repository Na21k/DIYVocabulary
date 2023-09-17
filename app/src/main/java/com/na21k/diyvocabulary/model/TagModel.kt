package com.na21k.diyvocabulary.model

import com.na21k.diyvocabulary.TITLE_FIELD_NAME
import java.io.Serializable

class TagModel : UserOwnedModel(), Serializable {

    var title: String? = null

    override fun toMap(): Map<String, Any?> {
        val res = mutableMapOf<String, Any?>()
        res.putAll(super.toMap())
        res.putAll(
            mapOf(
                TITLE_FIELD_NAME to title
            )
        )

        return res
    }
}
