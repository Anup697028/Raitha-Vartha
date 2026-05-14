package com.raitha.vartha

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.databinding.ActivityMainBinding
import com.raitha.vartha.utils.LocaleHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefManager: PreferenceManager

    override fun attachBaseContext(newBase: Context) {
        val prefManager = PreferenceManager(newBase)
        val language = runBlocking { prefManager.languageFlow.first() }
        super.attachBaseContext(LocaleHelper.updateBaseContextLocale(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefManager = PreferenceManager(this)
        
        // Observe theme and apply
        lifecycleScope.launch {
            prefManager.themeFlow.collect { theme ->
                when (theme) {
                    "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.languageFragment, R.id.districtFragment, R.id.cropSelectionFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }
}
