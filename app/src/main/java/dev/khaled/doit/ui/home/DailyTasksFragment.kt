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
import dev.khaled.doit.data.model.TaskPriority
import dev.khaled.doit.databinding.FragmentDailyTasksBinding
import java.util.UUID

@AndroidEntryPoint
class DailyTasksFragment : Fragment(), DailyTaskDialogFragment.OnTaskAddedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyTaskAdapter
    private val dailyTaskItems = mutableListOf<DailyTask>()
    private lateinit var binding: FragmentDailyTasksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyTasksBinding.inflate(inflater, container, false)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.rvTasks
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = DailyTaskAdapter()
        recyclerView.adapter = adapter

        addTestItems()

        binding.btnAddTask.setOnClickListener{
            showAddTaskDialog()
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_DailyTasksFragment_to_HomeFragment)
        }
    }

    private fun addTestItems() {
        dailyTaskItems.addAll(listOf(
            DailyTask(
                id = UUID.randomUUID().toString(),
                text = "Complete Project Documentation"
            ),
            DailyTask(
                id = UUID.randomUUID().toString(),
                text = "Complete Project Documentation"
            )
        ))
        adapter.updateTasks(dailyTaskItems)
    }

    private fun showAddTaskDialog() {
        val dialog = DailyTaskDialogFragment()
        dialog.setOnTaskAddedListener(this)
        dialog.show(childFragmentManager, DailyTaskDialogFragment.TAG)
    }

    override fun onTaskAdded(
        title: String,
        description: String,
        priority: TaskPriority
    ) {
        val newItem = DailyTask(
            id = UUID.randomUUID().toString(),
            text = title
        )
        dailyTaskItems.add(newItem)
        adapter.updateTasks(dailyTaskItems)
        
        view?.let {
            Snackbar.make(it, "Task added: $title", Snackbar.LENGTH_SHORT).show()
        }
    }
}