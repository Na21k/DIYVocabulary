package com.na21k.diyvocabulary.ui.home.word.attachedImagesList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.na21k.diyvocabulary.databinding.ImagesListAddImageFooterBinding
import com.na21k.diyvocabulary.databinding.ImagesListItemBinding
import com.na21k.diyvocabulary.model.AttachedImageModel
import com.na21k.diyvocabulary.ui.home.word.attachedImagesList.viewHolders.AddImageViewHolder
import com.na21k.diyvocabulary.ui.home.word.attachedImagesList.viewHolders.ImageItemViewHolder
import com.na21k.diyvocabulary.ui.home.word.attachedImagesList.viewHolders.ImagesListViewHolderBase
import com.na21k.diyvocabulary.ui.shared.listWithFooterItems.FOOTER_ITEM
import com.na21k.diyvocabulary.ui.shared.listWithFooterItems.FooterListItem
import com.na21k.diyvocabulary.ui.shared.listWithFooterItems.ListWithFooterItem
import com.na21k.diyvocabulary.ui.shared.listWithFooterItems.NORMAL_ITEM
import com.na21k.diyvocabulary.ui.shared.listWithFooterItems.NormalListItem

class AttachedImagesListAdapter(
    private val onImageActionRequestedListener: OnImageActionRequestedListener
) : RecyclerView.Adapter<ImagesListViewHolderBase>() {

    private var mItems: List<ListWithFooterItem> = listOf(FooterListItem())
        set(value) {
            val mutable = mutableListOf<ListWithFooterItem>()
            mutable.addAll(value)
            mutable.add(FooterListItem())
            field = mutable
        }
    private val mNormalItemsOnly: List<NormalListItem<AttachedImageModel>>
        get() {
            return if (mItems.size == 1) emptyList()
            else mItems.subList(0, mItems.lastIndex) as List<NormalListItem<AttachedImageModel>>
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesListViewHolderBase {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == FOOTER_ITEM) {
            val binding = ImagesListAddImageFooterBinding.inflate(inflater, parent, false)
            AddImageViewHolder(binding, onImageActionRequestedListener)
        } else {
            val binding = ImagesListItemBinding.inflate(inflater, parent, false)
            ImageItemViewHolder(binding, onImageActionRequestedListener)
        }
    }

    override fun onBindViewHolder(holder: ImagesListViewHolderBase, position: Int) {
        val viewType = getItemViewType(position)

        if (viewType == NORMAL_ITEM) {
            val imageItem = mItems[position] as NormalListItem<AttachedImageModel>
            val viewHolder = holder as ImageItemViewHolder

            viewHolder.setData(imageItem.model)
        }
    }

    override fun getItemCount(): Int = mItems.size

    override fun getItemViewType(position: Int): Int = mItems[position].getType()

    fun setItems(items: List<AttachedImageModel>) {
        val newItems = mutableListOf<ListWithFooterItem>()

        newItems.addAll(items.map {
            NormalListItem(it)
        })

        mItems = newItems
        notifyDataSetChanged()
    }

    fun getItems(): List<AttachedImageModel> = mNormalItemsOnly.map { it.model }

    fun addItem(item: AttachedImageModel) {
        val newItems = mutableListOf<ListWithFooterItem>()
        newItems.addAll(mNormalItemsOnly)
        newItems.add(NormalListItem(item))

        mItems = newItems
        notifyItemInserted(newItems.lastIndex)
    }

    fun deleteItem(item: AttachedImageModel) {
        val position = mNormalItemsOnly.indexOfFirst { it.model == item }

        if (position == -1) {
            return
        }

        val newItems = mutableListOf<ListWithFooterItem>()
        newItems.addAll(mNormalItemsOnly)
        newItems.removeAt(position)

        mItems = newItems
        notifyItemRemoved(position)
    }
}
