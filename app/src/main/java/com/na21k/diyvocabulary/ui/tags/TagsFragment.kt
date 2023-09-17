package com.na21k.diyvocabulary.ui.tags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.na21k.diyvocabulary.BaseFragment
import com.na21k.diyvocabulary.MainActivitySharedViewModel
import com.na21k.diyvocabulary.databinding.FragmentTagsBinding
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.ui.tags.tagDialog.TAG_MODEL_ARG_KEY
import com.na21k.diyvocabulary.ui.tags.tagDialog.TagDialogFragment

class TagsFragment : BaseFragment(), TagsListAdapter.OnTagActionListener {

    private lateinit var mBinding: FragmentTagsBinding
    private lateinit var mViewModel: MainActivitySharedViewModel
    private lateinit var mListAdapter: TagsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity())[MainActivitySharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTagsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListAdapter = setUpRecyclerView()
        setListeners()
        observeLiveData()
    }

    private fun setUpRecyclerView(): TagsListAdapter {
        val rv = mBinding.tagsListRecyclerView
        val adapter = TagsListAdapter(this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        return adapter
    }

    override fun setListeners() {
        mBinding.addTagFab.setOnClickListener {
            val tagDialog = TagDialogFragment()
            tagDialog.show(parentFragmentManager, null)
        }
    }

    override fun observeLiveData() {
        mViewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Snackbar.make(mBinding.root, it.message.toString(), Snackbar.LENGTH_INDEFINITE)
                    .show()
                mViewModel.consumeError()
            }
        }
        mViewModel.tags.observe(viewLifecycleOwner) {
            mListAdapter.setItems(it)
        }
    }

    override fun tagOpen(tag: TagModel) {
        val tagDialog = TagDialogFragment()

        val argsBundle = Bundle()
        argsBundle.putSerializable(TAG_MODEL_ARG_KEY, tag)
        tagDialog.arguments = argsBundle

        tagDialog.show(parentFragmentManager, null)
    }

    override fun tagDelete(tag: TagModel) {
        mViewModel.deleteTag(tag)
    }
}
