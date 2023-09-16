package com.na21k.diyvocabulary.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.na21k.diyvocabulary.R

const val WORD_DOCUMENT_ID_ARG_KEY = "wordDocumentIdArgKey"

class WordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)
    }
}
