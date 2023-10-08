package com.na21k.diyvocabulary.ui.home.word.attachedImagesList

import com.na21k.diyvocabulary.model.AttachedImageModel

interface OnImageActionRequestedListener {

    fun onImageOpenRequested(image: AttachedImageModel)
    fun onImageDeletionRequested(image: AttachedImageModel)
    fun onImageAdditionRequested()
}
