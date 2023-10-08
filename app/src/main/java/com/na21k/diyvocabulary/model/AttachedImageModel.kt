package com.na21k.diyvocabulary.model

import com.google.firebase.storage.StorageReference

class AttachedImageModel(
    val fileName: String,
    val deviceFileUri: String?,
    val downloadLinkUri: StorageReference?
) :
    UserOwnedModel()
