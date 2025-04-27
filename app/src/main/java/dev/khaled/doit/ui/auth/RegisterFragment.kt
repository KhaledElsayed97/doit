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
import dev.khaled.doit.data.model.User
import dev.khaled.doit.databinding.FragmentRegisterBinding
import dev.khaled.doit.util.UiState
import dev.khaled.doit.util.hide
import dev.khaled.doit.util.isValidEmail
import dev.khaled.doit.util.show
import dev.khaled.doit.util.snackbar

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    val TAG: String = "RegisterFragment"
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.btnStart.setOnClickListener {
            if (validation()){
                viewModel.register(
                    email = binding.tvEmail.text.toString(),
                    password = binding.tvPassword.text.toString(),
                    user = getUserObj()
                )
            }
        }
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.loader.show()
                }
                is UiState.Failure -> {
                    binding.loader.hide()
                    snackbar("Registration error")
                }
                is UiState.Success -> {
                    binding.loader.hide()
                    snackbar(state.data)
                    (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
                    startActivity(Intent(activity, MainActivity::class.java))
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.tvName.text.isNullOrEmpty()){
            isValid = false
            snackbar(getString(R.string.enter_name))
        }

        if (binding.tvEmail.text.isNullOrEmpty()){
            isValid = false
            snackbar(getString(R.string.enter_email))
        }else{
            if (!binding.tvEmail.text.toString().isValidEmail()){
                isValid = false
                snackbar(getString(R.string.invalid_email))
            }
        }
        if (binding.tvPassword.text.isNullOrEmpty()){
            isValid = false
            snackbar(getString(R.string.enter_password))
        }else{
            if (binding.tvPassword.text.toString().length < 8){
                isValid = false
                snackbar(getString(R.string.invalid_password))
            }
        }
        return isValid
    }

    private fun getUserObj(): User {
        return User(
            id = "",
            name = binding.tvName.text.toString(),
            email = binding.tvEmail.text.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}