package dev.khaled.doit.data.repo

import dev.khaled.doit.data.model.User
import dev.khaled.doit.util.UiState

interface AuthRepo {
    fun login(email: String, password: String, result: (UiState<String>) -> Unit)
    fun signup(email: String, password: String,user: User, result: (UiState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String, result: (User?) -> Unit)
    fun getSession(result: (User?) -> Unit)
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
    fun checkUserExists(id: String, result: (Boolean) -> Unit)
}