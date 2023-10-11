package com.na21k.diyvocabulary.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.FragmentHomeBinding
import com.na21k.diyvocabulary.model.WordModel
import com.na21k.diyvocabulary.ui.MainActivitySharedViewModel
import com.na21k.diyvocabulary.ui.home.word.WORD_MODEL_ARG_KEY
import com.na21k.diyvocabulary.ui.home.word.WordActivity
import com.na21k.diyvocabulary.ui.shared.BaseFragment

class HomeFragment : BaseFragment(), WordsListAdapter.OnWordActionListener, MenuProvider {

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

        val menuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListAdapter = setUpRecyclerView()
        setListeners()
        observeLiveData()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_fragment_options_menu, menu)

        val searchItemActionView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchItemActionView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) mListAdapter.search(newText)
                else mListAdapter.clearSearch()

                return false
            }
        })
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false

    private fun setUpRecyclerView(): WordsListAdapter {
        val rv = mBinding.wordsListRecyclerView
        val adapter = WordsListAdapter(this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        return adapter
    }

    override fun setListeners() {
        mBinding.addWordFab.setOnClickListener {
            val addWordIntent = Intent(context, WordActivity::class.java)
            startActivity(addWordIntent)
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
        mViewModel.wordsWithTags.observe(viewLifecycleOwner) {
            mListAdapter.setItems(it)
        }
    }

    override fun wordOpen(word: WordModel) {
        val addWordIntent = Intent(context, WordActivity::class.java)
        addWordIntent.putExtra(WORD_MODEL_ARG_KEY, word)
        startActivity(addWordIntent)
    }

    override fun wordDelete(word: WordModel) {
        mViewModel.deleteWord(word)
    }
}
