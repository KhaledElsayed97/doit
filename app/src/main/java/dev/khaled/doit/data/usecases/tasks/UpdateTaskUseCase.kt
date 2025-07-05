package dev.khaled.doit.data.usecases.tasks

import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.repo.TaskRepo
import dev.khaled.doit.data.usecases.UseCase
import dev.khaled.doit.util.UiState
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

data class UpdateTaskParams(
    val task: Task
)

class UpdateTaskUseCase @Inject constructor(
    private val taskRepo: TaskRepo
) : UseCase<UpdateTaskParams, UiState<Pair<Task, String>>> {
    override suspend fun invoke(parameters: UpdateTaskParams): UiState<Pair<Task, String>> {
        return suspendCancellableCoroutine { continuation ->
            taskRepo.updateTask(parameters.task) { result ->
                continuation.resume(result)
            }
        }
    }
} 