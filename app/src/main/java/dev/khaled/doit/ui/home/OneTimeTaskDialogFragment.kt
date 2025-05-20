package dev.khaled.doit.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.khaled.doit.R
import dev.khaled.doit.data.model.TaskPriority
import dev.khaled.doit.databinding.DialogOneTimeTaskBinding
import dev.khaled.doit.ui.OneTimeTask
import java.text.SimpleDateFormat
import java.util.*

class OneTimeTaskDialogFragment : DialogFragment() {

    private var onTaskAddedListener: OnTaskAddedListener? = null
    private var selectedDate: Long? = null
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0
    private var selectedPriority: TaskPriority = TaskPriority.MEDIUM
    private var existingTask: OneTimeTask? = null

    private lateinit var binding: DialogOneTimeTaskBinding

    interface OnTaskAddedListener {
        fun onTaskAdded(title: String, description: String, dueDate: Date?, priority: TaskPriority)
        fun onTaskEdited(task: OneTimeTask, title: String, description: String, dueDate: Date?, priority: TaskPriority)
    }

    fun setOnTaskAddedListener(listener: OnTaskAddedListener) {
        onTaskAddedListener = listener
    }

    fun setExistingTask(task: OneTimeTask) {
        existingTask = task
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogOneTimeTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleInput = binding.tilTitle
        val descriptionInput = binding.tilDescription
        val dateInput = binding.tilDate
        val timeInput = binding.tilTime
        val priorityInput = binding.tilPrio

        // Setup priority dropdown
        val priorities = TaskPriority.values().map { it.displayName }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_item,
            priorities
        )
        priorityInput.setAdapter(adapter)

        // If editing existing task, populate fields
        existingTask?.let { task ->
            binding.tvDialogTitle.text = "Edit Task"
            titleInput.setText(task.title)
            descriptionInput.setText(task.description)
            selectedPriority = task.priority
            priorityInput.setText(task.priority.displayName, false)
        } ?: run {
            binding.tvDialogTitle.text = "Add New Task"
            priorityInput.setText(TaskPriority.MEDIUM.displayName, false)
        }

        priorityInput.setOnItemClickListener { _, _, position, _ ->
            selectedPriority = TaskPriority.values()[position]
        }

        // Date picker
        dateInput.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select due date")
                .setSelection(selectedDate ?: MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedDate = selection
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                dateInput.setText(dateFormat.format(Date(selection)))
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        // Time picker
        timeInput.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(selectedHour)
                .setMinute(selectedMinute)
                .setTitleText("Select due time")
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                selectedHour = timePicker.hour
                selectedMinute = timePicker.minute
                timeInput.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }

            timePicker.show(parentFragmentManager, "TIME_PICKER")
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()

            if (title.isNotEmpty()) {
                val dueDate = selectedDate?.let { date ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = date
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    calendar.set(Calendar.MINUTE, selectedMinute)
                    calendar.time
                }
                
                if (existingTask != null) {
                    onTaskAddedListener?.onTaskEdited(existingTask!!, title, description, dueDate, selectedPriority)
                } else {
                    onTaskAddedListener?.onTaskAdded(title, description, dueDate, selectedPriority)
                }
                dismiss()
            } else {
                titleInput.error = "Title is required"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        const val TAG = "OneTimeTaskDialogFragment"
    }
}