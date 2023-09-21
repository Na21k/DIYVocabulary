package com.na21k.diyvocabulary.ui.home

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.na21k.diyvocabulary.BaseActivity
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.ActivityWordBinding
import com.na21k.diyvocabulary.databinding.TagChipViewBinding
import com.na21k.diyvocabulary.helpers.setTextIfEmpty
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.model.WordModel
import com.na21k.diyvocabulary.ui.home.pickTagDialog.PickTagDialogFragment
import com.na21k.diyvocabulary.ui.tags.tagDialog.TagDialogFragment
import java.text.DateFormat

const val WORD_MODEL_ARG_KEY = "wordModelArgKey"

class WordActivity : BaseActivity(), PickTagDialogFragment.OnPickTagDialogFragmentActionListener,
    TagDialogFragment.OnTagDialogFragmentActionListener {

    private lateinit var mBinding: ActivityWordBinding
    private lateinit var mViewModel: WordActivityViewModel
    private val wordExtra: WordModel?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getSerializable(WORD_MODEL_ARG_KEY, WordModel::class.java)
        } else {
            intent.extras?.getSerializable(WORD_MODEL_ARG_KEY) as WordModel?
        }
    private val isExistingDocument get() = wordExtra != null
    private var mWord = WordModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.appBar.appBar)

        mViewModel = ViewModelProvider(this)[WordActivityViewModel::class.java]

        enableUpNavigation(mBinding.appBar.appBar)

        displayIfExistingDocument()
        displayTags()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.word_activity_options_menu, menu)

        if (!isExistingDocument) {
            menu?.removeItem(R.id.delete)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                save()
                true
            }

            R.id.delete -> {
                delete()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayIfExistingDocument() {
        if (!isExistingDocument) {
            return
        }

        val word = wordExtra

        mWord = word!!
        setTextIfEmpty(mBinding.word, word.word)
        setTextIfEmpty(mBinding.transcription, word.transcription)
        setTextIfEmpty(mBinding.translation, word.translation)
        setTextIfEmpty(mBinding.explanation, word.explanation)
        setTextIfEmpty(mBinding.usageExample, word.usageExample)
        mBinding.lastModified.text = getString(
            R.string.last_modified_formatted,
            word.lastModified?.toDate()?.let { DateFormat.getDateTimeInstance().format(it) }
        )

        mBinding.lastModified.visibility = View.VISIBLE
    }

    private fun displayTags() {
        mBinding.tags.removeAllViews()

        mWord.tagModels?.forEach { tagModel ->
            val tagChipBinding = TagChipViewBinding.inflate(layoutInflater)
            tagChipBinding.root.text = tagModel.title
            tagChipBinding.root.closeIcon =
                AppCompatResources.getDrawable(this, R.drawable.ic_remove_24)
            tagChipBinding.root.isCloseIconVisible = true

            tagChipBinding.root.setOnCloseIconClickListener {
                removeTag(tagModel, tagChipBinding.root)
            }

            mBinding.tags.addView(tagChipBinding.root)
        }

        displayAddTagChip()
    }

    private fun removeTag(tagModel: TagModel, tagChip: Chip) {
        mWord.removeTag(tagModel)
        mBinding.tags.removeView(tagChip)
    }

    private fun displayAddTagChip() {
        val addTagChipBinding = TagChipViewBinding.inflate(layoutInflater)
        addTagChipBinding.root.text = getString(R.string.add_tag_chip)
        addTagChipBinding.root.chipIcon =
            AppCompatResources.getDrawable(this, R.drawable.ic_add_24)

        addTagChipBinding.root.setOnClickListener {
            val pickTagDialog = PickTagDialogFragment()
            pickTagDialog.show(supportFragmentManager, null)
        }

        mBinding.tags.addView(addTagChipBinding.root)
    }

    private fun save() {
        if (mBinding.word.text.isNullOrBlank()) {
            mBinding.wordLayout.error = getString(R.string.validation_required)
            return
        }

        mWord.word = mBinding.word.text.toString()
        mWord.transcription = mBinding.transcription.text.toString()
        mWord.translation = mBinding.translation.text.toString()
        mWord.explanation = mBinding.explanation.text.toString()
        mWord.usageExample = mBinding.usageExample.text.toString()

        mViewModel.save(mWord)
        finish()
    }

    private fun delete() {
        mViewModel.delete(mWord)
        finish()
    }

    override fun onPicked(tagModel: TagModel) {
        mWord.addTag(tagModel)
        displayTags()
    }

    override fun onSaveTag(tag: TagModel) {
        mViewModel.saveTag(tag)
    }

    override fun onDeleteTag(tag: TagModel) {
        //no implementation required, the user can't open existing tags from this activity
    }
}
