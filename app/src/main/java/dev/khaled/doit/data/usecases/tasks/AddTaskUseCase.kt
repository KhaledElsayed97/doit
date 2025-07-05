package dev.khaled.doit.data.usecases.tasks

import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.repo.TaskRepo
import dev.khaled.doit.data.usecases.UseCase
import dev.khaled.doit.util.UiState
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

data class AddTaskParams(
    val task: Task
)

class AddTaskUseCase @Inject constructor(
    private val taskRepo: TaskRepo
) : UseCase<AddTaskParams, UiState<Pair<Task, String>>> {
    override suspend fun invoke(parameters: AddTaskParams): UiState<Pair<Task, String>> {
        return suspendCancellableCoroutine { continuation ->
            taskRepo.addTask(parameters.task) { result ->
                continuation.resume(result)
            }
        }
    }
} 