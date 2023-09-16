package com.na21k.diyvocabulary.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.na21k.diyvocabulary.BaseActivity
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.databinding.ActivityWordBinding
import com.na21k.diyvocabulary.model.WordModel
import java.text.DateFormat

const val WORD_DOCUMENT_ID_ARG_KEY = "wordDocumentIdArgKey"

class WordActivity : BaseActivity(), WordActivityViewModel.OnFetchListener {

    private lateinit var mBinding: ActivityWordBinding
    private lateinit var mViewModel: WordActivityViewModel
    private val wordDocumentId get() = intent.extras?.getString(WORD_DOCUMENT_ID_ARG_KEY)
    private var mWord = WordModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.appBar.appBar)

        mViewModel = ViewModelProvider(this)[WordActivityViewModel::class.java]

        enableUpNavigation(mBinding.appBar.appBar)

        val isExistingDocument = wordDocumentId != null

        if (isExistingDocument) {
            fetchWord()
        }
    }

    private fun fetchWord() {
        mViewModel.fetchWord(wordDocumentId!!, this)
    }

    override fun onFetched(word: WordModel?) {
        if (word == null) {
            return
        }

        mWord = word
        mBinding.word.setText(word.word)
        mBinding.transcription.setText(word.transcription)
        mBinding.translation.setText(word.translation)
        mBinding.explanation.setText(word.explanation)
        mBinding.usageExample.setText(word.usageExample)
        mBinding.lastModified.text = getString(
            R.string.last_modified_formatted,
            word.lastModified?.toDate()?.let { DateFormat.getDateTimeInstance().format(it) }
        )

        mBinding.lastModified.visibility = View.VISIBLE
    }

    override fun onError(exception: Exception?) {
        Snackbar.make(mBinding.root, exception?.message.toString(), Snackbar.LENGTH_INDEFINITE)
            .show()
    }
}
