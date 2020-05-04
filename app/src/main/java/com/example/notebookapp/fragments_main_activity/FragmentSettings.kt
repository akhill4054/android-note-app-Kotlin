package com.example.notebookapp.fragments_main_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notebookapp.MainActivity

import com.example.notebookapp.R


class FragmentSettings : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Setting up the toolbar
        val toolbar = (requireActivity() as MainActivity).toolbar
        toolbar.menu.clear()

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

}
