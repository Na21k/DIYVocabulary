package com.na21k.diyvocabulary.ui.home

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.na21k.diyvocabulary.BaseViewHolder
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.WordsListItemViewBinding
import com.na21k.diyvocabulary.model.WordModel

class WordsListAdapter(private val mOnWordActionListener: OnWordActionListener) :
    RecyclerView.Adapter<WordsListAdapter.WordViewHolder>() {

    private var mItems: List<WordModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WordsListItemViewBinding.inflate(inflater, parent, false)

        return WordViewHolder(binding)
    }

    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val item = mItems[position]
        holder.setData(item)
    }

    fun setItems(items: List<WordModel>) {
        mItems = items
        notifyDataSetChanged()
    }

    inner class WordViewHolder(private val binding: WordsListItemViewBinding) :
        BaseViewHolder(binding.root, R.menu.word_context_menu, 0) {

        private lateinit var mItemModel: WordModel

        fun setData(item: WordModel) {
            mItemModel = item

            binding.word.text = item.word
            binding.transcription.text = item.transcription
            binding.translation.text = item.translation
            binding.explanation.text = item.explanation
            binding.attachedImagesCount.text =
                binding.root.resources.getString(R.string.image_count_formatted, 100500)

            updateViewsVisibility()

            itemView.setOnClickListener {
                mOnWordActionListener.wordOpen(item)
            }
        }

        private fun updateViewsVisibility() {
            binding.transcription.isVisible = !mItemModel.transcription.isNullOrEmpty()
            binding.translation.isVisible = !mItemModel.translation.isNullOrEmpty()
            binding.explanation.isVisible = !mItemModel.explanation.isNullOrEmpty()
            binding.attachedImagesCount.isVisible = true    //TODO if images count > 0
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.delete -> {
                    mOnWordActionListener.wordDelete(mItemModel)
                    true
                }

                else -> false
            }
        }
    }

    interface OnWordActionListener {
        fun wordOpen(word: WordModel)
        fun wordDelete(word: WordModel)
    }
}
