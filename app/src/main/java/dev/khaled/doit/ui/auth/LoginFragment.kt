package dev.khaled.doit.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.MainActivity
import dev.khaled.doit.R
import dev.khaled.doit.databinding.FragmentLoginBinding
import dev.khaled.doit.ui.home.HomeActivity
import dev.khaled.doit.util.UiState
import dev.khaled.doit.util.hide
import dev.khaled.doit.util.isValidEmail
import dev.khaled.doit.util.show
import dev.khaled.doit.util.snackbar

@AndroidEntryPoint
class LoginFragment : Fragment() {

    val TAG: String = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.tvSignUp.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, RegisterFragment())
                ?.commit();
        }
        binding.btnLogin.setOnClickListener {
            if (validation()) {
                viewModel.login(
                    email = binding.tvEmail.text.toString(),
                    password = binding.tvPassword.text.toString()
                )
            }
        }
        binding.tvForgotPassword.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, ForgotPasswordFragment())
                ?.commit();
        }
    }

    private fun observer(){
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.loader.show()
                }
                is UiState.Failure -> {
                    binding.loader.hide()
                    snackbar("error")
                }
                is UiState.Success -> {
                    binding.loader.hide()
                    snackbar(state.data)
                    startActivity(Intent(activity, HomeActivity::class.java))
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.tvEmail.text.isNullOrEmpty()){
            isValid = false
            snackbar(getString(R.string.enter_email))
        }else{
            if (!binding.tvEmail.text.toString().isValidEmail()){
                isValid = false
                snackbar(getString(R.string.invalid_login))
            }
        }
        if (binding.tvPassword.text.isNullOrEmpty()){
            isValid = false
            snackbar(getString(R.string.enter_password))
        }else{
            if (binding.tvPassword.text.toString().length < 8){
                isValid = false
                snackbar(getString(R.string.invalid_login))
            }
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}