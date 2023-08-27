package com.example.diyvocabulary.ui.tags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.diyvocabulary.R

class TagsFragment : Fragment() {

    companion object {
        fun newInstance() = TagsFragment()
    }

    private lateinit var viewModel: TagsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tags, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TagsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
