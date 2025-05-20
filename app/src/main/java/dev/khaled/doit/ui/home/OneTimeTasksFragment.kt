package dev.khaled.doit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R
import dev.khaled.doit.data.model.Task
import dev.khaled.doit.data.model.TaskPriority
import dev.khaled.doit.databinding.FragmentOneTimeTasksBinding
import dev.khaled.doit.ui.OneTimeTask
import dev.khaled.doit.ui.Priority
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class OneTimeTasksFragment : Fragment(), OneTimeTaskDialogFragment.OnTaskAddedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OneTimeTaskAdapter
    private val oneTimeTasks = mutableListOf<OneTimeTask>()
    private lateinit var binding: FragmentOneTimeTasksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOneTimeTasksBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.rvTasks
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = OneTimeTaskAdapter(
            onItemClick = { item ->
                showEditTaskDialog(item)
            },
            onCompleteClick = { item ->
                // Handle complete click
                val index = oneTimeTasks.indexOf(item)
                if (index != -1) {
                    oneTimeTasks[index] = item.copy(isCompleted = true)
                    adapter.updateTasks(oneTimeTasks)
                }
            },
            onDeleteClick = { item ->
                // Handle delete click
                oneTimeTasks.removeAt(oneTimeTasks.indexOf(item))
                adapter.updateTasks(oneTimeTasks)
            },
            onPostponeClick = { item ->
                // Handle postpone click
                Snackbar.make(view, "Postponed: ${item.title}", Snackbar.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter

        addTestItems()

        binding.btnAddTask.setOnClickListener{
            showAddTaskDialog()
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_OneTimeTasksFragment_to_HomeFragment)
        }
    }

    private fun addTestItems() {
        oneTimeTasks.addAll(listOf(
            OneTimeTask(
                id = UUID.randomUUID().toString(),
                title = "Complete Project",
                description = "Description",
                priority = TaskPriority.LOW
            ),
            OneTimeTask(
                id = UUID.randomUUID().toString(),
                title = "Complete Project Documentation",
                description = "Description",
                priority = TaskPriority.MEDIUM
            )
        ))
        adapter.updateTasks(oneTimeTasks)
    }

    private fun showAddTaskDialog() {
        val dialog = OneTimeTaskDialogFragment()
        dialog.setOnTaskAddedListener(this)
        dialog.show(childFragmentManager, OneTimeTaskDialogFragment.TAG)
    }

    private fun showEditTaskDialog(task: OneTimeTask) {
        val dialog = OneTimeTaskDialogFragment()
        dialog.setOnTaskAddedListener(this)
        dialog.setExistingTask(task)
        dialog.show(childFragmentManager, OneTimeTaskDialogFragment.TAG)
    }

    override fun onTaskAdded(
        title: String,
        description: String,
        dueDate: Date?,
        priority: TaskPriority
    ) {
        val newItem = OneTimeTask(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            priority = priority
        )
        oneTimeTasks.add(newItem)
        adapter.updateTasks(oneTimeTasks)

        view?.let {
            Snackbar.make(it, "Task added: $title", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onTaskEdited(
        task: OneTimeTask,
        title: String,
        description: String,
        dueDate: Date?,
        priority: TaskPriority
    ) {
        val index = oneTimeTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            oneTimeTasks[index] = OneTimeTask(
                id = task.id,
                title = title,
                description = description,
                priority = priority,
                isCompleted = task.isCompleted
            )
            adapter.updateTasks(oneTimeTasks)

            view?.let {
                Snackbar.make(it, "Task updated: $title", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            oneTimeTasks.add(OneTimeTask(
                id = task.id,
                title = title,
                description = description,
                priority = priority,
                isCompleted = task.isCompleted
            ))
            adapter.updateTasks(oneTimeTasks)

            view?.let {
                Snackbar.make(it, "Task added: $title", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}