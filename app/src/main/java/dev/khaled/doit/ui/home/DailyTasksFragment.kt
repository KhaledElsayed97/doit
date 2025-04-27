package dev.khaled.doit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R
import dev.khaled.doit.data.model.TaskPriority
import dev.khaled.doit.ui.AddTaskDialogFragment
import java.util.Date

@AndroidEntryPoint
class DailyTasksFragment : Fragment(), AddTaskDialogFragment.OnTaskAddedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_tasks, container, false)
        
        view.findViewById<ExtendedFloatingActionButton>(R.id.add_task_button).setOnClickListener {
            showAddTaskDialog()
        }
        
        return view
    }

    private fun showAddTaskDialog() {
        val dialog = AddTaskDialogFragment()
        dialog.setOnTaskAddedListener(this)
        dialog.show(childFragmentManager, AddTaskDialogFragment.TAG)
    }

    override fun onTaskAdded(
        title: String,
        description: String,
        dueDate: Date?,
        priority: TaskPriority
    ) {
        // TODO: Handle the new task (save to database, update UI, etc.)
        view?.let {
            Snackbar.make(it, "Task added: $title", Snackbar.LENGTH_SHORT).show()
        }
    }
}