package com.na21k.diyvocabulary.ui.tags

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.TagsListItemViewBinding
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.ui.shared.viewHolders.BaseViewHolder

class TagsListAdapter(private val onTagActionListener: OnTagActionListener) :
    RecyclerView.Adapter<TagsListAdapter.TagViewHolder>() {

    private var mItems: List<TagModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TagsListItemViewBinding.inflate(inflater, parent, false)

        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val item = mItems[position]
        holder.setData(item)
    }

    fun setItems(items: List<TagModel>) {
        mItems = items
        notifyDataSetChanged()
    }

    inner class TagViewHolder(private val binding: TagsListItemViewBinding) :
        BaseViewHolder(binding.root, R.menu.tag_context_menu, 0) {

        private lateinit var mItemModel: TagModel

        fun setData(item: TagModel) {
            mItemModel = item

            binding.tagTitle.text = item.title
            itemView.setOnClickListener {
                onTagActionListener.tagOpen(item)
            }
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.delete -> {
                    onTagActionListener.tagDelete(mItemModel)
                    true
                }

                else -> false
            }
        }
    }

    interface OnTagActionListener {
        fun tagOpen(tag: TagModel)
        fun tagDelete(tag: TagModel)
    }
}
