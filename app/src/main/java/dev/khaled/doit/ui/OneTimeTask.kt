package dev.khaled.doit.ui

import dev.khaled.doit.data.model.TaskPriority

data class OneTimeTask(
    val id: String,
    val title: String,
    val description: String,
    val priority: TaskPriority,
    val isCompleted: Boolean = false
)

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
} 