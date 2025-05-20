package dev.khaled.doit.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.khaled.doit.R
import dev.khaled.doit.data.model.TaskPriority
import dev.khaled.doit.databinding.ItemTodoBinding
import dev.khaled.doit.ui.Priority
import dev.khaled.doit.ui.OneTimeTask

class OneTimeTaskAdapter(
    private val onItemClick: (OneTimeTask) -> Unit,
    private val onCompleteClick: (OneTimeTask) -> Unit,
    private val onDeleteClick: (OneTimeTask) -> Unit,
    private val onPostponeClick: (OneTimeTask) -> Unit
) : RecyclerView.Adapter<OneTimeTaskAdapter.TodoViewHolder>() {

    private var items: List<OneTimeTask> = emptyList()

    private lateinit var binding: ItemTodoBinding

    fun updateTasks(newItems: List<OneTimeTask>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: OneTimeTask) {

            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

                // Set priority color based on the item's priority
                val priority = when (item.priority) {
                    TaskPriority.LOW -> R.drawable.ic_prio_low
                    TaskPriority.MEDIUM -> R.drawable.ic_prio_mid
                    TaskPriority.HIGH -> R.drawable.ic_prio_high
                }

//                if(item.isCompleted)
//                    llTodoItem.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.md_theme_dark_tertiary))
//                else
//                    llTodoItem.setBackgroundColor(ContextCompat.getColor(itemView.context,priorityColor))

                ivPrio.setImageDrawable(ContextCompat.getDrawable(itemView.context,priority))

                // Set up options menu
                btnOptions.setOnClickListener {
                    showOptionsMenu(item)
                }

                // Handle item click
                itemView.setOnClickListener {
                    onItemClick(item)
                }
            }
        }

        private fun showOptionsMenu(item: OneTimeTask) {
            val popup = PopupMenu(itemView.context, itemView.findViewById(R.id.btnOptions))
            popup.inflate(R.menu.todo_item_menu)
            
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_complete -> {
                        onCompleteClick(item)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(item)
                        true
                    }
                    R.id.action_postpone -> {
                        onPostponeClick(item)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
} 