package dev.khaled.doit.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.khaled.doit.data.model.User
import dev.khaled.doit.data.repo.AuthRepo
import dev.khaled.doit.util.UiState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepo
): ViewModel() {

    private val TAG = "AuthViewModel"
    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword

    fun register(email: String, password: String, user: User) {
        _register.value = UiState.Loading
        repository.signup(email = email, password = password, user = user) {
            _register.value = it
        }
    }

    fun login(email: String, password: String) {
        _login.value = UiState.Loading
        repository.login(email, password) {
            _login.value = it
        }
    }

    fun forgotPassword(email: String) {
        _forgotPassword.value = UiState.Loading
        repository.forgotPassword(email) {
            _forgotPassword.value = it
        }
    }

    fun logout(result: () -> Unit){
        repository.logout(result)
    }

    fun getSession(result: (User?) -> Unit){
        repository.getSession(result)
    }

    fun checkUserExists(id: String, result: (Boolean) -> Unit) {
        repository.checkUserExists(id, result)
    }
}