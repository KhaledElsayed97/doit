package dev.khaled.doit.data.usecases.tasks

import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.model.User
import dev.khaled.doit.data.repo.TaskRepo
import dev.khaled.doit.data.usecases.UseCase
import dev.khaled.doit.util.UiState
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class GetTasksParams(
    val user: User?
)

class GetTasksUseCase @Inject constructor(
    private val taskRepo: TaskRepo
) : UseCase<GetTasksParams, UiState<List<Task>>> {
    override suspend fun invoke(parameters: GetTasksParams): UiState<List<Task>> {
        return suspendCancellableCoroutine { continuation ->
            taskRepo.getTasks(parameters.user) { result ->
                continuation.resume(result)
            }
        }
    }
} 