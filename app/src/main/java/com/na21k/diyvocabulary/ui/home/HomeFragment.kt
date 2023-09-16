package com.na21k.diyvocabulary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.na21k.diyvocabulary.BaseFragment
import com.na21k.diyvocabulary.MainActivitySharedViewModel
import com.na21k.diyvocabulary.databinding.FragmentHomeBinding
import com.na21k.diyvocabulary.model.WordModel

class HomeFragment : BaseFragment(), WordsListAdapter.OnWordActionListener {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mViewModel: MainActivitySharedViewModel
    private lateinit var mListAdapter: WordsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity())[MainActivitySharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListAdapter = setUpRecyclerView()
        setListeners()
        observeLiveData()
    }

    private fun setUpRecyclerView(): WordsListAdapter {
        val rv = mBinding.wordsListRecyclerView
        val adapter = WordsListAdapter(this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        return adapter
    }

    override fun setListeners() {

    }

    override fun observeLiveData() {
        mViewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Snackbar.make(mBinding.root, it.message.toString(), Snackbar.LENGTH_INDEFINITE)
                    .show()
                mViewModel.consumeError()
            }
        }
        mViewModel.wordsWithTags.observe(viewLifecycleOwner) {
            mListAdapter.setItems(it)
        }
    }

    override fun wordOpen(word: WordModel) {
        TODO("Not yet implemented")
    }

    override fun wordDelete(word: WordModel) {
        mViewModel.deleteWord(word)
    }
}
