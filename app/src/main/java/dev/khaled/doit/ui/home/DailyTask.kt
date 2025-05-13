package dev.khaled.doit.ui.home

data class DailyTask(
    val id: String,
    val text: String,
    var isCompleted: Boolean = false
) 