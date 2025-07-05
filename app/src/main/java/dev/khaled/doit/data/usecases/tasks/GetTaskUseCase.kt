package dev.khaled.doit.data.usecases.tasks

import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.repo.TaskRepo
import dev.khaled.doit.data.usecases.UseCase
import dev.khaled.doit.util.UiState
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

data class GetTaskParams(
    val id: String
)

class GetTaskUseCase @Inject constructor(
    private val taskRepo: TaskRepo
) : UseCase<GetTaskParams, UiState<Pair<Task, String>>> {
    override suspend fun invoke(parameters: GetTaskParams): UiState<Pair<Task, String>> {
        return suspendCancellableCoroutine { continuation ->
            taskRepo.getTask(parameters.id) { result ->
                continuation.resume(result)
            }
        }
            
    }
} 