package dev.khaled.doit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.model.User
import dev.khaled.doit.data.repo.AuthRepo
import dev.khaled.doit.data.repo.TaskRepo
import dev.khaled.doit.util.UiState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val taskRepo: TaskRepo
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?>
        get() = _currentUser

    private val _tasks = MutableLiveData<UiState<List<Task>>>()
    val tasks: LiveData<UiState<List<Task>>>
        get() = _tasks

    private val _taskOperation = MutableLiveData<UiState<Pair<Task, String>>>()
    val taskOperation: LiveData<UiState<Pair<Task, String>>>
        get() = _taskOperation

    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate>
        get() = _selectedDate

    init {
        getCurrentUser()
        // Initialize with today's date
        _selectedDate.value = LocalDate.now()
    }

    private fun getCurrentUser() {
        authRepo.getSession { user ->
            _currentUser.value = user
            user?.let { loadTasks(it) }
        }
    }

    fun loadTasks(user: User) {
        _tasks.value = UiState.Loading
        taskRepo.getTasks(user) { state ->
            _tasks.value = state
        }
    }

    fun addTask(task: Task) {
        _taskOperation.value = UiState.Loading
        taskRepo.addTask(task) { state ->
            _taskOperation.value = state
            if (state is UiState.Success) {
                loadTasks(_currentUser.value ?: return@addTask)
            }
        }
    }

    fun updateTask(task: Task) {
        _taskOperation.value = UiState.Loading
        taskRepo.updateTask(task) { state ->
            _taskOperation.value = state
            if (state is UiState.Success) {
                loadTasks(_currentUser.value ?: return@updateTask)
            }
        }
    }

    fun deleteTask(task: Task) {
        _taskOperation.value = UiState.Loading
        taskRepo.deleteTask(task) { state ->
            _taskOperation.value = state
            if (state is UiState.Success) {
                loadTasks(_currentUser.value ?: return@deleteTask)
            }
        }
    }

    fun completeTask(task: Task) {
        _taskOperation.value = UiState.Loading
        taskRepo.completeTask(task) { state ->
            _taskOperation.value = state
            if (state is UiState.Success) {
                loadTasks(_currentUser.value ?: return@completeTask)
            }
        }
    }

    fun unCompleteTask(task: Task) {
        _taskOperation.value = UiState.Loading
        taskRepo.unCompleteTask(task) { state ->
            _taskOperation.value = state
            if (state is UiState.Success) {
                loadTasks(_currentUser.value ?: return@unCompleteTask)
            }
        }
    }

    fun logout() {
        authRepo.logout {
            _currentUser.value = null
            _tasks.value = UiState.Success(emptyList())
        }
    }

    fun updateUserInfo(user: User) {
        authRepo.updateUserInfo(user) { state ->
            if (state is UiState.Success) {
                getCurrentUser()
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun getSelectedDate(): LocalDate {
        return _selectedDate.value ?: LocalDate.now()
    }
} 