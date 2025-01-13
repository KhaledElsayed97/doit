package dev.khaled.doit.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.model.User
import dev.khaled.doit.util.FireStoreCollection
import dev.khaled.doit.util.UiState
import javax.inject.Inject

class TaskRepoImpl @Inject constructor(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore
) : TaskRepo {

    override fun addTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit) {
        database.collection(FireStoreCollection.TASKS)
            .document(auth.currentUser?.uid ?: "")
            .collection(FireStoreCollection.TASKS)
            .add(task)
            .addOnSuccessListener {
                result.invoke(UiState.Success(Pair(task,"Task added successfully!")))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
    }

    override fun updateTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit) {
        database.collection(FireStoreCollection.TASKS)
            .document(auth.currentUser?.uid ?: "")
            .collection(FireStoreCollection.TASKS)
            .document(task.id)
            .set(task)
            .addOnSuccessListener {
                result.invoke(UiState.Success(Pair(task, "Task updated successfully!")))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
    }

    override fun deleteTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit) {
        database.collection(FireStoreCollection.TASKS)
            .document(auth.currentUser?.uid ?: "")
            .collection(FireStoreCollection.TASKS)
            .document(task.id)
            .delete()
            .addOnSuccessListener {
                result.invoke(UiState.Success(Pair(task, "Task deleted successfully!")))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
    }

    override fun completeTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getTasks(user: User?, result: (UiState<List<Task>>) -> Unit) {
        database.collection(FireStoreCollection.TASKS)
            .document(auth.currentUser?.uid ?: "")
            .collection(FireStoreCollection.TASKS)
            .get()
            .addOnSuccessListener {
                val tasks = it.toObjects(Task::class.java)
                result.invoke(UiState.Success(tasks.filterNotNull()))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
    }

    override fun getTask(id: String, result: (UiState<Pair<Task,String>>) -> Unit) {
        database.collection(FireStoreCollection.TASKS)
            .document(auth.currentUser?.uid ?: "")
            .collection(FireStoreCollection.TASKS)
            .document(id)
            .get()
            .addOnSuccessListener {
                val task = it.toObject(Task::class.java)
                if(task != null) {
                    result.invoke(UiState.Success(Pair(task, "Task deleted successfully!")))
                } else {
                    result.invoke(UiState.Failure("Task not found!"))
                }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
    }

    override fun unCompleteTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun storeTasks(tasks: List<Task>, result: (UiState<String>) -> Unit) {
        database.collection(FireStoreCollection.TASKS)
            .document(auth.currentUser?.uid ?: "")
            .collection(FireStoreCollection.TASKS)
            .get()
            .addOnSuccessListener {
                val tasksInDatabase = it.toObjects(Task::class.java)
                val tasksToAdd = tasks.filter { task ->
                    tasksInDatabase.find { it.id == task.id } == null
                }
                val tasksToUpdate = tasks.filter { task ->
                    tasksInDatabase.find { it.id == task.id } != null
                }
                val tasksToDelete = tasksInDatabase.filter { task ->
                    tasks.find { it.id == task.id } == null
                }
                tasksToAdd.forEach { task ->
                    database.collection(FireStoreCollection.TASKS)
                        .document(auth.currentUser?.uid ?: "")
                        .collection(FireStoreCollection.TASKS)
                        .add(task)
                }
                tasksToUpdate.forEach { task ->
                    database.collection(FireStoreCollection.TASKS)
                        .document(auth.currentUser?.uid ?: "")
                        .collection(FireStoreCollection.TASKS)
                        .document(task.id)
                        .set(task)
                }
                tasksToDelete.forEach { task ->
                    database.collection(FireStoreCollection.TASKS)
                        .document(auth.currentUser?.uid ?: "")
                        .collection(FireStoreCollection.TASKS)
                        .document(task.id)
                        .delete()
                }
                result.invoke(UiState.Success("Tasks stored successfully!"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
    }
}