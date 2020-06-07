package com.example.notebookapp.fragments_main_activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.example.notebookapp.LIGHT_THEME_KEY
import com.example.notebookapp.MainActivity
import com.example.notebookapp.R
import kotlinx.android.synthetic.main.fragment_settings.*

class FragmentSettings : Fragment() {

    private var isLightThemeEnabled = false
    lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).let { mainActivity ->
            mainActivity.hideActionButton()
            isLightThemeEnabled = mainActivity.isLightThemeEnabled
            prefs = mainActivity.prefs
        }

        val layout = layoutInflater.inflate(R.layout.fragment_settings, container, false)
        layout.findViewById<Switch>(R.id.theme_switch).isChecked = isLightThemeEnabled

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editor = prefs.edit()

        theme_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                editor.putBoolean(LIGHT_THEME_KEY, true)
            } else {
                editor.putBoolean(LIGHT_THEME_KEY, false)
            }
            editor.apply()
//            (requireActivity() as MainActivity).changeTheme()
            activity?.recreate()
        }
    }

}
