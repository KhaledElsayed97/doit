package dev.khaled.doit.data.usecases.tasks

import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.repo.TaskRepo
import dev.khaled.doit.data.usecases.UseCase
import dev.khaled.doit.util.UiState
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

data class StoreTasksParams(
    val tasks: List<Task>
)

class StoreTasksUseCase @Inject constructor(
    private val taskRepo: TaskRepo
) : UseCase<StoreTasksParams, UiState<String>> {
    override suspend fun invoke(parameters: StoreTasksParams): UiState<String> {
        return suspendCancellableCoroutine { continuation ->
            taskRepo.storeTasks(parameters.tasks) { result ->
                continuation.resume(result)
            }
        }
    }
} 