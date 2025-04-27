package dev.khaled.doit.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R
import dev.khaled.doit.databinding.FragmentLandingBinding
import dev.khaled.doit.ui.home.HomeActivity

@AndroidEntryPoint
class LandingFragment : Fragment() {

    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
//            findNavController().navigate(R.id.action_landingFragment_to_loginFragment)
            val intent = Intent(requireContext(), HomeActivity::class.java)

            // Start the SecondActivity
            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}