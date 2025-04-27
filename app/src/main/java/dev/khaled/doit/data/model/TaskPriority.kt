package dev.khaled.doit.data.model

enum class TaskPriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    companion object {
        fun fromDisplayName(displayName: String): TaskPriority {
            return values().find { it.displayName == displayName } ?: MEDIUM
        }
    }
}