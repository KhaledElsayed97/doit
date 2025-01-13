package dev.khaled.doit.data.repo

import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.model.User
import dev.khaled.doit.util.UiState

interface TaskRepo {
    fun addTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun getTasks(user: User?,result: (UiState<List<Task>>) -> Unit)
    fun updateTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun deleteTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun completeTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun getTask(id: String, result: (UiState<Pair<Task,String>>) -> Unit)
    fun unCompleteTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit)
    fun storeTasks(tasks: List<Task>, result: (UiState<String>) -> Unit)
}