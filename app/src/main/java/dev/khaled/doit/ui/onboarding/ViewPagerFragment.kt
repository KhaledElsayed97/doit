package dev.khaled.doit.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R
import dev.khaled.doit.databinding.FragmentViewPagerBinding
import dev.khaled.doit.ui.onboarding.screens.FirstOnboardingFragment
import dev.khaled.doit.ui.onboarding.screens.SecondOnboardingFragment
import dev.khaled.doit.ui.onboarding.screens.ThirdOnboardingFragment

@AndroidEntryPoint
class ViewPagerFragment : Fragment() {

    private lateinit var binding : FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        binding = FragmentViewPagerBinding.bind(view)

        val fragmentList = arrayListOf<Fragment>(
            FirstOnboardingFragment(),
            SecondOnboardingFragment(),
            ThirdOnboardingFragment()
        )

        val adapter = ViewPagerAdapter(fragmentList,requireActivity().supportFragmentManager,lifecycle)

        binding.viewPager.adapter = adapter

        return view
    }

}