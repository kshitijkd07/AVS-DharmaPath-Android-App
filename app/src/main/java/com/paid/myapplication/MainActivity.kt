package com.paid.myapplication

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.paid.myapplication.databinding.ActivityMainBinding
import com.paid.myapplication.viewmodel.AppViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: AppViewModel by viewModels()

    private val mainDestinations = setOf(
        R.id.homeFragment,
        R.id.jaapFragment,
        R.id.palmReadingFragment,
        R.id.pujaFragment,
        R.id.profileFragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme(viewModel.theme.value)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Wire bottom nav — uses menu item IDs matching fragment IDs
        binding.bottomNav.setupWithNavController(navController)

        // Show/hide bottom nav
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility =
                if (destination.id in mainDestinations) View.VISIBLE else View.GONE
        }

        // Navigate to correct start screen only on first launch (no saved state)
        if (savedInstanceState == null) {
            val isAuthenticated = viewModel.isAuthenticated.value == true
            val hasOnboarded = viewModel.hasOnboarded.value == true

            when {
                !hasOnboarded -> navController.navigate(R.id.onboardingFragment)
                !isAuthenticated -> navController.navigate(R.id.loginFragment)
                // Already on homeFragment (nav graph default) — do nothing
            }
        }

        // Observe theme changes
        lifecycleScope.launch {
            viewModel.theme.collect { theme ->
                val newMode = if (theme == "dark")
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
                if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                    AppCompatDelegate.setDefaultNightMode(newMode)
                }
            }
        }
    }

    private fun applyTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(
            if (theme == "dark") AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
