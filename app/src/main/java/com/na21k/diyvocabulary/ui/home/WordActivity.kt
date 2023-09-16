package com.na21k.diyvocabulary.ui.home

import android.os.Bundle
import com.na21k.diyvocabulary.BaseActivity
import com.na21k.diyvocabulary.databinding.ActivityWordBinding

const val WORD_DOCUMENT_ID_ARG_KEY = "wordDocumentIdArgKey"

class WordActivity : BaseActivity() {

    private lateinit var mBinding: ActivityWordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.appBar.appBar)

        //mViewModel = ...

        enableUpNavigation(mBinding.appBar.appBar)
    }
}
