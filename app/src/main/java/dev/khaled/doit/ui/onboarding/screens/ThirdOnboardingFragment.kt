package dev.khaled.doit.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R

@AndroidEntryPoint
class ThirdOnboardingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_onboarding, container, false)

        view.findViewById<TextView>(R.id.tv_finish).setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_landingFragment)
        }

        return view
    }

}