package dev.khaled.doit.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.textfield.TextInputEditText
import dev.khaled.doit.R
import dev.khaled.doit.data.model.TaskPriority
import java.text.SimpleDateFormat
import java.util.*

class AddTaskDialogFragment : DialogFragment() {

    private var onTaskAddedListener: OnTaskAddedListener? = null
    private var selectedDate: Long? = null
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0
    private var selectedPriority: TaskPriority = TaskPriority.MEDIUM

    interface OnTaskAddedListener {
        fun onTaskAdded(title: String, description: String, dueDate: Date?, priority: TaskPriority)
    }

    fun setOnTaskAddedListener(listener: OnTaskAddedListener) {
        onTaskAddedListener = listener
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
        return inflater.inflate(R.layout.dialog_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleInput = view.findViewById<TextInputEditText>(R.id.taskTitleInput)
        val descriptionInput = view.findViewById<TextInputEditText>(R.id.taskDescriptionInput)
        val dateInput = view.findViewById<TextInputEditText>(R.id.dateInput)
        val timeInput = view.findViewById<TextInputEditText>(R.id.timeInput)
        val priorityInput = view.findViewById<AutoCompleteTextView>(R.id.priorityInput)

        // Setup priority dropdown
        val priorities = TaskPriority.values().map { it.displayName }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_item,
            priorities
        )
        priorityInput.setAdapter(adapter)
        priorityInput.setText(TaskPriority.MEDIUM.displayName, false)
        priorityInput.setOnItemClickListener { _, _, position, _ ->
            selectedPriority = TaskPriority.values()[position]
        }

        // Date picker
        view.findViewById<View>(R.id.dateInput).setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select due date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedDate = selection
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                dateInput.setText(dateFormat.format(Date(selection)))
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        // Time picker
        view.findViewById<View>(R.id.timeInput).setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
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

        view.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }

        view.findViewById<View>(R.id.saveButton).setOnClickListener {
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
                
                onTaskAddedListener?.onTaskAdded(title, description, dueDate, selectedPriority)
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
        const val TAG = "AddTaskDialogFragment"
    }
}