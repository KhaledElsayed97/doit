package dev.khaled.doit.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R
import dev.khaled.doit.databinding.FragmentForgotPasswordBinding
import dev.khaled.doit.util.UiState
import dev.khaled.doit.util.hide
import dev.khaled.doit.util.isValidEmail
import dev.khaled.doit.util.show
import dev.khaled.doit.util.snackbar

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    val TAG: String = "ForgotPasswordFragment"
    lateinit var binding: FragmentForgotPasswordBinding
    val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.forgotPasswordButton.setOnClickListener {
            if (validation()){
                viewModel.forgotPassword(binding.username.text.toString())
            }
        }
    }

    private fun observer(){
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.loading.show()
                }
                is UiState.Failure -> {
                    binding.loading.hide()
                }
                is UiState.Success -> {
                    binding.loading.hide()
                    snackbar(state.data)
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container, LoginFragment())
                        ?.commit();
                }
            }
        }
    }

    fun validation(): Boolean {
        var isValid = true
        if (binding.username.text.isNullOrEmpty()){
            isValid = false
        }else{
            if (!binding.username.text.toString().isValidEmail()){
                isValid = false
            }
        }
        return isValid
    }
}