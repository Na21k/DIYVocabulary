package com.na21k.diyvocabulary.model

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class AttachedImageModel(
    var fileName: String,
    var deviceFileUri: String?,
    var downloadLinkUri: StorageReference?
) :
    UserOwnedModel() {
    private fun writeObject(outObj: java.io.ObjectOutputStream) {
        outObj.writeObject(fileName)
        outObj.writeObject(deviceFileUri)
        outObj.writeObject(downloadLinkUri?.path)
    }

    private fun readObject(inObj: java.io.ObjectInputStream) {
        fileName = inObj.readObject() as String
        deviceFileUri = inObj.readObject() as String?
        downloadLinkUri = (inObj.readObject() as String?)?.let { Firebase.storage.getReference(it) }
    }
}
