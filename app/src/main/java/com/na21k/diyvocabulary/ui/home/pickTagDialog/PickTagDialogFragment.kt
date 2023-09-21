package com.na21k.diyvocabulary.ui.home.pickTagDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.ui.home.WordActivityViewModel
import com.na21k.diyvocabulary.ui.tags.tagDialog.TagDialogFragment

class PickTagDialogFragment : DialogFragment() {

    private lateinit var mViewModel: WordActivityViewModel
    private lateinit var mOnPickTagDialogFragmentActionListener: OnPickTagDialogFragmentActionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OnPickTagDialogFragmentActionListener) {
            throw IllegalArgumentException(
                "${context::class.java} must implement " +
                        "${OnPickTagDialogFragmentActionListener::class.java}"
            )
        }

        mOnPickTagDialogFragmentActionListener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity())[WordActivityViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.create_tag_alert_button) { _, _ -> createTag() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mViewModel.allTagsCache
        )
        dialogBuilder.setAdapter(adapter) { _, which ->
            val selectedTag = adapter.getItem(which)
            selectedTag?.let { mOnPickTagDialogFragmentActionListener.onPicked(it) }
        }

        return dialogBuilder.create()
    }

    private fun createTag() {
        val tagDialog = TagDialogFragment()
        tagDialog.show(parentFragmentManager, null)
    }

    interface OnPickTagDialogFragmentActionListener {
        fun onPicked(tagModel: TagModel)
    }
}
