package dev.khaled.doit.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R

@AndroidEntryPoint
class SecondOnboardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second_onboarding, container, false)

        val viewPager= activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.findViewById<TextView>(R.id.tv_next).setOnClickListener {
            viewPager?.currentItem = 2
        }

        return view
    }
}