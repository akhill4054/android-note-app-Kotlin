package com.example.notebookapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var actionButton: FloatingActionButton
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actionButton = btn_action

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
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