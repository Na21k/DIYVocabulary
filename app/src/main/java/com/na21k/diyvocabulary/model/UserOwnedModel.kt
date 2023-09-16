package com.na21k.diyvocabulary.model

import com.na21k.diyvocabulary.USER_ID_FIELD_NAME

abstract class UserOwnedModel : IdentifiableModel() {

    var userId: String? = null

    open fun toMap(): Map<String, Any?> {
        return mapOf(
            USER_ID_FIELD_NAME to userId
        )
    }
}
