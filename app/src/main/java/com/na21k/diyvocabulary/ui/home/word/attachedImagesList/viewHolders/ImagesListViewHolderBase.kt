package com.na21k.diyvocabulary.ui.home.word.attachedImagesList.viewHolders

import android.view.View
import com.na21k.diyvocabulary.ui.home.word.attachedImagesList.OnImageActionRequestedListener
import com.na21k.diyvocabulary.ui.shared.viewHolders.BaseViewHolder

abstract class ImagesListViewHolderBase(
    itemView: View,
    contextMenuRes: Int,
    contextMenuHeaderRes: Int,
    val onImageActionRequestedListener: OnImageActionRequestedListener
) : BaseViewHolder(itemView, contextMenuRes, contextMenuHeaderRes)
