package com.example.notebookapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.notebookapp.fragments_main_activity.FragmentArchive
import com.example.notebookapp.fragments_main_activity.FragmentBin
import com.example.notebookapp.fragments_main_activity.FragmentNewNote
import com.example.notebookapp.fragments_main_activity.FragmentNoteList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

const val PREFS_FILENAME = "com.example.notebook.prefs"
const val LIGHT_THEME_KEY = "LIGHT_THEME"
const val THEME_CHANGED = "THEME_CHANGED"

class MainActivity : AppCompatActivity() {

    private lateinit var actionButton: FloatingActionButton
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: Toolbar
    lateinit var overlayToolbar: Toolbar
    lateinit var prefs: SharedPreferences
    var isLightThemeEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Getting prefs and setting up user prefs
        prefs = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        isLightThemeEnabled = prefs.getBoolean(LIGHT_THEME_KEY, false)
        if (isLightThemeEnabled)
            setTheme(R.style.LightTheme)

        setContentView(R.layout.activity_main)

        actionButton = btn_action

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)

        toolbar = findViewById(R.id.toolbar)
        overlayToolbar = findViewById(R.id.overlay_toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        val navView = findViewById<NavigationView>(R.id.nav_view)

        navView.setupWithNavController(navController)

        if (intent.getBooleanExtra(THEME_CHANGED, false)) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentSettings)
            navView.setCheckedItem(R.id.fragmentSettings)
        }
    }

    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = findNavController(R.id.nav_host_fragment)

        navHost?.let { navFragment ->
            // Handling list selection cancellation
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                navController.currentDestination?.id.let {id ->
                    if (id == R.id.fragmentNoteList) {
                        val fragmentNoteList = fragment as FragmentNoteList
                        if (fragmentNoteList.isSelectionOn) {
                            fragmentNoteList.cancelSelection()
                            return
                        }
                    } else if (id == R.id.fragmentNewNote) {
                        val fragmentNewNote = fragment as FragmentNewNote
                        // Saving note on back press
                        fragmentNewNote.saveNote()
                    } else if (id == R.id.fragmentArchive) {
                        val fragmentArchive = fragment as FragmentArchive
                        if (fragmentArchive.adapter.isSelectionOn) {
                            fragmentArchive.canceLSelection()
                            return
                        }
                    } else if (id == R.id.fragmentBin) {
                        val fragmentBin = fragment as FragmentBin
                        if (fragmentBin.adapter.isSelectionOn) {
                            fragmentBin.canceLSelection()
                            return
                        }
                    }
                }
            }
        }

        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)

        return super.onOptionsItemSelected(item) || item.onNavDestinationSelected(navController)
    }

    @SuppressLint("RestrictedApi")
    fun showActionButton() {
        actionButton.visibility = View.VISIBLE

        actionButton.animate()
            .alpha(0.2F)
            .scaleX(50F)
            .scaleY(50F)
            .scaleXBy(-0.8F)
            .scaleYBy(-0.8F)
            .setDuration(0L)
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    actionButton.animate()
                        .scaleX(50F)
                        .scaleY(50F)
                        .scaleXBy(0.8F)
                        .scaleYBy(0.8F)
                        .alpha(1F)
                        .setDuration(200L)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                actionButton.isClickable = true
                            }
                        })

                }
            })
    }

    @SuppressLint("RestrictedApi")
    fun hideActionButton() {
        actionButton.isClickable = false
        actionButton.visibility = View.INVISIBLE
    }

    fun changeTheme() {
        finish()
        Intent(this, MainActivity::class.java).let {
            it.putExtra(THEME_CHANGED, true)
            startActivity(it)
        }
    }

}