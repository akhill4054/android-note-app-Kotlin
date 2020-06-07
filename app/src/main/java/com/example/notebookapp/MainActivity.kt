package com.example.notebookapp


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.notebookapp.fragments_main_activity.FragmentArchive
import com.example.notebookapp.fragments_main_activity.FragmentBin
import com.example.notebookapp.fragments_main_activity.FragmentNewNote
import com.example.notebookapp.fragments_main_activity.FragmentNoteList
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

const val PREFS_FILENAME = "com.example.notebook.prefs"
const val LIGHT_THEME_KEY = "LIGHT_THEME"
const val THEME_CHANGED = "THEME_CHANGED"

class MainActivity : AppCompatActivity() {

    private lateinit var actionButton: FloatingActionButton
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: MaterialToolbar
    lateinit var overlayToolbar: Toolbar
    lateinit var prefs: SharedPreferences
    var isLightThemeEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Getting prefs and setting up user prefs
        prefs = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        isLightThemeEnabled = prefs.getBoolean(LIGHT_THEME_KEY, false)
        if (isLightThemeEnabled) {
            setTheme(R.style.LightTheme)
            window.statusBarColor = ContextCompat.getColor(this, R.color.lightToolbarColor)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        setContentView(R.layout.activity_main)

        actionButton = btn_action

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)

//        overlayToolbar = overlay_toolbar
        toolbar = findViewById(R.id.toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        val navView = findViewById<NavigationView>(R.id.nav_view)

        navView.setupWithNavController(navController)

        // Handle theme change
//        if (intent.getBooleanExtra(THEME_CHANGED, false)) {
//            findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentSettings)
//            toolbar.setupWithNavController(navController, appBarConfiguration)
//            navView.setCheckedItem(R.id.fragmentSettings)
//            intent.putExtra(THEME_CHANGED, false)
//        }
    }

    @SuppressLint("RtlHardcoded")
    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = findNavController(R.id.nav_host_fragment)

        // If nav drawer is open
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            // Closing nav drawer
            drawer_layout.closeDrawer(Gravity.LEFT)
            return
        }

        navHost?.let { navFragment ->
            // Handling list selection cancellation
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                navController.currentDestination?.id.let { id ->
                    if (id == R.id.fragmentNewNote) {
                        val fragmentNewNote = fragment as FragmentNewNote
                        // Saving note on back press
                        fragmentNewNote.saveNote()
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
        actionButton.run {
            visibility = View.VISIBLE
            val anim = AnimationUtils.loadAnimation(context, R.anim.fab_show)

            clearAnimation()
            startAnimation(anim)
        }
    }

    @SuppressLint("RestrictedApi")
    fun hideActionButton(animation: Boolean = false) {
        actionButton.isClickable = false

        if (animation) {
            actionButton.run {
                val anim = AnimationUtils.loadAnimation(context, R.anim.fab_hide)
                anim.setAnimationListener(
                    object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}

                        override fun onAnimationEnd(animation: Animation?) {
                            visibility = View.INVISIBLE
                        }
                    }
                )

                clearAnimation()
                startAnimation(anim)
            }
        } else
            actionButton.visibility = View.INVISIBLE
    }

//    fun changeTheme() {
//        recreate()
//        finish()
//        Intent(this, MainActivity::class.java).let {
//            it.putExtra(THEME_CHANGED, true)
//            startActivity(it)
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        }
//    }

}