package com.example.notebookapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var actionButton: FloatingActionButton
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: Toolbar
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
        toolbar.setupWithNavController(navController, appBarConfiguration)

        val navView = findViewById<NavigationView>(R.id.nav_view)

        navView.setupWithNavController(navController)
    }

    override fun onRestart() {
        super.onRestart()

        if (isLightThemeEnabled != prefs.getBoolean(LIGHT_THEME_KEY, false)) {
            Intent(this, MainActivity::class.java).let {
                finish()
                startActivity(it)
            }
        }
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

}