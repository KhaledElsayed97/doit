package dev.khaled.doit.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.khaled.doit.R

class DailyTaskAdapter(
    private var tasks: List<DailyTask> = emptyList()
) : RecyclerView.Adapter<DailyTaskAdapter.DailyTaskViewHolder>() {

    class DailyTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskText: TextView = view.findViewById(R.id.taskText)
        val checkmarkCircle: ImageView = view.findViewById(R.id.checkmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyTaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_task, parent, false)
        return DailyTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyTaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskText.text = task.text
        
        holder.itemView.setOnClickListener {
            task.isCompleted = !task.isCompleted
            holder.checkmarkCircle.visibility = if (task.isCompleted) View.VISIBLE else View.GONE
        }
        
        holder.checkmarkCircle.visibility = if (task.isCompleted) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<DailyTask>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
} 