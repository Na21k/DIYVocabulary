package com.na21k.diyvocabulary.ui.tags.tagDialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.FragmentTagDialogBinding
import com.na21k.diyvocabulary.helpers.setTextIfEmpty
import com.na21k.diyvocabulary.model.TagModel

const val TAG_MODEL_ARG_KEY = "tagModelArgKey"

class TagDialogFragment : DialogFragment() {

    private lateinit var mBinding: FragmentTagDialogBinding
    private lateinit var mOnDialogActionListener: OnTagDialogFragmentActionListener
    private val tagArg
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(TAG_MODEL_ARG_KEY, TagModel::class.java)
        } else {
            arguments?.getSerializable(TAG_MODEL_ARG_KEY) as TagModel?
        }
    private val isExistingDocument get() = tagArg != null
    private var mTag: TagModel = TagModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OnTagDialogFragmentActionListener) {
            throw IllegalArgumentException(
                "${context::class.java} must implement " +
                        "${OnTagDialogFragmentActionListener::class.java}"
            )
        }

        mOnDialogActionListener = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        mBinding = FragmentTagDialogBinding.inflate(layoutInflater)
        mBinding.title.requestFocus()
        dialogBuilder.setView(mBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                mTag.title = mBinding.title.text.toString()
                mOnDialogActionListener.onSaveTag(mTag)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }

        if (isExistingDocument) {
            dialogBuilder.setNeutralButton(R.string.delete) { _, _ ->
                mOnDialogActionListener.onDeleteTag(mTag)
            }
        }

        val dialog = dialogBuilder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        displayIfExistingDocument()

        return dialog
    }

    private fun displayIfExistingDocument() {
        if (!isExistingDocument) {
            return
        }

        mTag = tagArg!!
        setTextIfEmpty(mBinding.title, tagArg!!.title)
        mBinding.title.selectAll()
    }

    interface OnTagDialogFragmentActionListener {
        fun onSaveTag(tag: TagModel)
        fun onDeleteTag(tag: TagModel)
    }
}
