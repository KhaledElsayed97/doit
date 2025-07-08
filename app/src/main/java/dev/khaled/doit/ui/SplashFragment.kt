package dev.khaled.doit.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R
import dev.khaled.doit.data.repo.AuthRepo
import dev.khaled.doit.ui.home.HomeActivity
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject
    lateinit var authRepo: AuthRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Add a small delay for splash screen effect
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, 1500) // 1.5 seconds delay
    }

    private fun checkUserSession() {
        // First check if user is authenticated with Firebase Auth
        authRepo.isUserLoggedIn { isLoggedIn ->
            if (isLoggedIn) {
                // User is authenticated with Firebase, check local session
                authRepo.getSession { user ->
                    if (user != null) {
                        // User has both Firebase Auth and local session
                        android.util.Log.d("SplashFragment", "User fully authenticated: ${user.email}")
                        navigateToHome()
                    } else {
                        // User has Firebase Auth but no local session, restore it
                        android.util.Log.d("SplashFragment", "Firebase Auth found but no local session, restoring...")
                        restoreUserSession()
                    }
                }
            } else {
                // No Firebase Auth, check if there's a stale local session
                authRepo.getSession { user ->
                    if (user != null) {
                        // Stale local session, clear it
                        android.util.Log.d("SplashFragment", "Stale local session found, clearing...")
                        clearStaleSession()
                    }
                    navigateToOnboarding()
                }
            }
        }
    }

    private fun restoreUserSession() {
        // This will be handled by the existing storeSession logic in AuthRepoImpl
        // when the user navigates to HomeActivity
        navigateToHome()
    }

    private fun clearStaleSession() {
        // Clear the stale session
        val sharedPrefs = requireContext().getSharedPreferences("doit_preferences", 0)
        sharedPrefs.edit().remove("user_session").apply()
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToOnboarding() {
        findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
    }
}