package com.example.notebookapp

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.theme_switch

const val PREFS_FILENAME = "com.notebookapp.example"
const val LIGHT_THEME_KEY = "LIGHT_THEME"
const val KEY_THEME_CHANGED = "THEME_CHANGED"

class SettingsActivity : AppCompatActivity() {

    lateinit var prefs: SharedPreferences
    var isLightThemeEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Getting shared prefs
        prefs = getSharedPreferences(PREFS_FILENAME, 0)

        // Getting/setting theme prefs
        isLightThemeEnabled = prefs.getBoolean(LIGHT_THEME_KEY, false)
        if (isLightThemeEnabled) setTheme(R.style.LightTheme)

        setContentView(R.layout.activity_settings)

        // Setting up toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setting up user prefs
        if (isLightThemeEnabled)
            theme_switch.isChecked = isLightThemeEnabled

        // Saving shared prefs
        val prefsEditor = prefs.edit()

        theme_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (isLightThemeEnabled)
                    return@setOnCheckedChangeListener
                prefsEditor.putBoolean(LIGHT_THEME_KEY, true)
            } else {
                if (!isLightThemeEnabled)
                    return@setOnCheckedChangeListener
                prefsEditor.putBoolean(LIGHT_THEME_KEY, false)
            }

            prefsEditor.apply()

            Intent(this, SettingsActivity::class.java).let {
                finish()
                startActivity(it)
            }
        }
    }

}
