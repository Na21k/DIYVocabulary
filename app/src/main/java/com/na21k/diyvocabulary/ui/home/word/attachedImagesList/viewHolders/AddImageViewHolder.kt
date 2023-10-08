package com.na21k.diyvocabulary.ui.home.word.attachedImagesList.viewHolders

import android.view.MenuItem
import com.na21k.diyvocabulary.databinding.ImagesListAddImageFooterBinding
import com.na21k.diyvocabulary.ui.home.word.attachedImagesList.OnImageActionRequestedListener

class AddImageViewHolder(
    binding: ImagesListAddImageFooterBinding,
    onImageActionRequestedListener: OnImageActionRequestedListener
) : ImagesListViewHolderBase(
    binding.root,
    0, 0, onImageActionRequestedListener
) {
    init {
        itemView.setOnClickListener {
            this.onImageActionRequestedListener.onImageAdditionRequested()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean = false
}
