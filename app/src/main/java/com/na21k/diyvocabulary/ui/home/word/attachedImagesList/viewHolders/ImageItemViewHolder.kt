package com.na21k.diyvocabulary.ui.home.word.attachedImagesList.viewHolders

import android.view.MenuItem
import com.bumptech.glide.Glide
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.ImagesListItemBinding
import com.na21k.diyvocabulary.model.AttachedImageModel
import com.na21k.diyvocabulary.ui.home.word.attachedImagesList.OnImageActionRequestedListener

class ImageItemViewHolder(
    private val binding: ImagesListItemBinding,
    onImageActionRequestedListener: OnImageActionRequestedListener
) : ImagesListViewHolderBase(
    binding.root,
    R.menu.image_context_menu,
    0,
    onImageActionRequestedListener
) {
    private lateinit var mAttachedImage: AttachedImageModel

    init {
        itemView.setOnClickListener {
            this.onImageActionRequestedListener.onImageOpenRequested(mAttachedImage)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.image_delete_menu_item) {
            onImageActionRequestedListener.onImageDeletionRequested(mAttachedImage)
        }

        return true
    }

    fun setData(image: AttachedImageModel) {
        mAttachedImage = image

        Glide.with(itemView)
            .load(image.deviceFileUri ?: image.downloadLinkUri)
            .placeholder(R.drawable.ic_image_24)
            .error(R.drawable.ic_error_24)
            .into(binding.imageView)
    }
}
