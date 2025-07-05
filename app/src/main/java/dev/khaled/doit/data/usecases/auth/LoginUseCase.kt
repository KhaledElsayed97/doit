package dev.khaled.doit.data.usecases.auth

import dev.khaled.doit.data.repo.AuthRepo
import dev.khaled.doit.data.usecases.UseCase
import dev.khaled.doit.util.UiState
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

data class LoginParams(
    val email: String,
    val password: String
)

class LoginUseCase @Inject constructor(
    private val authRepo: AuthRepo
) : UseCase<LoginParams, UiState<String>> {
    override suspend fun invoke(parameters: LoginParams): UiState<String> {
        return suspendCancellableCoroutine { continuation ->
            authRepo.login(parameters.email, parameters.password) { result ->
                continuation.resume(result)
            }
        }
    }
} 