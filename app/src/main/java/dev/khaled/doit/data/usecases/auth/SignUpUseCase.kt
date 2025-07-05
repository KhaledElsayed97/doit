package dev.khaled.doit.data.usecases.auth

import dev.khaled.doit.data.model.User
import dev.khaled.doit.data.repo.AuthRepo
import dev.khaled.doit.data.usecases.UseCase
import dev.khaled.doit.util.UiState
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

data class SignUpParams(
    val email: String,
    val password: String,
    val user: User
)

class SignUpUseCase @Inject constructor(
    private val authRepo: AuthRepo
) : UseCase<SignUpParams, UiState<String>> {
    override suspend fun invoke(parameters: SignUpParams): UiState<String> {
        return suspendCancellableCoroutine { continuation ->
            authRepo.signup(
                email = parameters.email,
                password = parameters.password,
                user = parameters.user
            ) { result ->
                continuation.resume(result)
            }
        }
    }
} 