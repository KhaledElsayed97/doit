package dev.khaled.doit.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R
import dev.khaled.doit.data.repo.AuthRepo
import dev.khaled.doit.databinding.ActivityTasksBinding
import dev.khaled.doit.util.SharedPrefConstants
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTasksBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var authRepo: AuthRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupDrawer()
        updateNavigationHeader()
        checkAndRestoreSession()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.HomeFragment),
            binding.drawerLayout
        )
    }

    private fun setupDrawer() {
        binding.navView.setNavigationItemSelectedListener(this)
        
        // Listen to navigation changes to update selection
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateNavigationSelection(destination.id)
        }
    }

    private fun updateNavigationSelection(destinationId: Int) {
        // Clear all selections first
        binding.navView.menu.findItem(R.id.nav_home)?.isChecked = false
        binding.navView.menu.findItem(R.id.nav_daily_tasks)?.isChecked = false
        binding.navView.menu.findItem(R.id.nav_one_time_tasks)?.isChecked = false
        binding.navView.menu.findItem(R.id.nav_profile)?.isChecked = false
        
        // Set the current destination as selected
        when (destinationId) {
            R.id.HomeFragment -> binding.navView.menu.findItem(R.id.nav_home)?.isChecked = true
            R.id.DailyTasksFragment -> binding.navView.menu.findItem(R.id.nav_daily_tasks)?.isChecked = true
            R.id.OneTimeTasksFragment -> binding.navView.menu.findItem(R.id.nav_one_time_tasks)?.isChecked = true
            R.id.ProfileFragment -> binding.navView.menu.findItem(R.id.nav_profile)?.isChecked = true
        }
    }

    private fun updateNavigationHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val nameTextView = headerView.findViewById<android.widget.TextView>(R.id.nav_header_name)
        val emailTextView = headerView.findViewById<android.widget.TextView>(R.id.nav_header_email)

        authRepo.getSession { user ->
            runOnUiThread {
                if (user != null) {
                    nameTextView.text = user.name.ifEmpty { "User" }
                    emailTextView.text = user.email
                } else {
                    nameTextView.text = "User"
                    emailTextView.text = "user@email.com"
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        android.util.Log.d("HomeActivity", "Navigation item selected: ${item.title}")
        Toast.makeText(this, "Selected: ${item.title}", Toast.LENGTH_SHORT).show()
        
        when (item.itemId) {
            R.id.nav_home -> {
                android.util.Log.d("HomeActivity", "Navigating to HomeFragment")
                if (navController.currentDestination?.id != R.id.HomeFragment) {
                    navController.navigate(R.id.HomeFragment)
                }
            }
            R.id.nav_daily_tasks -> {
                android.util.Log.d("HomeActivity", "Navigating to DailyTasksFragment")
                if (navController.currentDestination?.id != R.id.DailyTasksFragment) {
                    navController.navigate(R.id.DailyTasksFragment)
                }
            }
            R.id.nav_one_time_tasks -> {
                android.util.Log.d("HomeActivity", "Navigating to OneTimeTasksFragment")
                if (navController.currentDestination?.id != R.id.OneTimeTasksFragment) {
                    navController.navigate(R.id.OneTimeTasksFragment)
                }
            }
            R.id.nav_profile -> {
                android.util.Log.d("HomeActivity", "Navigating to ProfileFragment")
                if (navController.currentDestination?.id != R.id.ProfileFragment) {
                    navController.navigate(R.id.ProfileFragment)
                }
            }
            R.id.nav_logout -> {
                android.util.Log.d("HomeActivity", "Logging out")
                logout()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        authRepo.logout {
            val sharedPrefs = getSharedPreferences(SharedPrefConstants.PREF_NAME, MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()
            
            val intent = Intent(this, dev.khaled.doit.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun checkAndRestoreSession() {
        // Check if user has Firebase Auth but no local session
        authRepo.isUserLoggedIn { isLoggedIn ->
            if (isLoggedIn) {
                authRepo.getSession { user ->
                    if (user == null) {
                        // User has Firebase Auth but no local session, restore it
                        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            android.util.Log.d("HomeActivity", "Restoring session for user: ${currentUser.uid}")
                            authRepo.storeSession(currentUser.uid) { restoredUser ->
                                if (restoredUser != null) {
                                    android.util.Log.d("HomeActivity", "Session restored successfully")
                                    updateNavigationHeader()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}