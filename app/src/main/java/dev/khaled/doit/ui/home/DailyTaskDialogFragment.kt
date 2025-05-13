package dev.khaled.doit.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import dev.khaled.doit.R
import dev.khaled.doit.data.model.TaskPriority
import dev.khaled.doit.databinding.DialogDailyTaskBinding


class DailyTaskDialogFragment : DialogFragment() {

    private var onTaskAddedListener: OnTaskAddedListener? = null
    private var selectedPriority: TaskPriority = TaskPriority.MEDIUM

    private lateinit var binding: DialogDailyTaskBinding

    interface OnTaskAddedListener {
        fun onTaskAdded(title: String, description: String, priority: TaskPriority)
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
        binding = DialogDailyTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val titleInput = binding.tilTitle
        val descriptionInput = binding.tilDescription
        val priorityInput = binding.tilPrio

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

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()

            if (title.isNotEmpty()) {
                onTaskAddedListener?.onTaskAdded(title, description, selectedPriority)
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
        const val TAG = "DailyTaskDialogFragment"
    }
}