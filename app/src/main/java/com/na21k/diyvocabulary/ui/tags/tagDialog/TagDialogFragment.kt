package com.na21k.diyvocabulary.ui.tags.tagDialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.FragmentTagDialogBinding
import com.na21k.diyvocabulary.model.TagModel

const val TAG_DOCUMENT_ID_ARG_KEY = "tagDocumentIdArgKey"

class TagDialogFragment : DialogFragment() {

    private lateinit var mBinding: FragmentTagDialogBinding
    private lateinit var mViewModel: TagDialogFragmentViewModel
    private lateinit var mOnDialogActionListener: OnTagDialogFragmentActionListener
    private val tagDocumentId get() = arguments?.getString(TAG_DOCUMENT_ID_ARG_KEY)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[TagDialogFragmentViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(context)
        mBinding = FragmentTagDialogBinding.inflate(layoutInflater)
        mBinding.title.requestFocus()
        dialogBuilder.setView(mBinding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                mTag.title = mBinding.title.text.toString()
                mOnDialogActionListener.onSaveTag(mTag)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setNeutralButton(R.string.delete) { _, _ ->
                mOnDialogActionListener.onDeleteTag(mTag)
            }

        val dialog = dialogBuilder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        val isExistingDocument = tagDocumentId != null

        if (isExistingDocument) {
            fetchTag()
        }

        return dialog
    }

    private fun fetchTag() {
        mViewModel.fetchTag(tagDocumentId!!, object : TagDialogFragmentViewModel.OnFetchListener {
            override fun onFetched(tag: TagModel?) {
                if (tag != null) {
                    mTag = tag
                    mBinding.title.setText(tag.title)
                    mBinding.title.selectAll()
                }
            }

            override fun onError(exception: Exception?) {
                AlertDialog.Builder(context)
                    .setMessage(exception?.message.toString())
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .show()

                dismiss()
            }
        })
    }

    interface OnTagDialogFragmentActionListener {
        fun onSaveTag(tag: TagModel)
        fun onDeleteTag(tag: TagModel)
    }
}
