package dev.khaled.doit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import dev.khaled.doit.R
import dev.khaled.doit.databinding.CalendarDayLayoutBinding
import dev.khaled.doit.databinding.CalendarHeaderLayoutBinding
import dev.khaled.doit.databinding.DialogCalendarBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class CalendarDialogFragment : DialogFragment() {

    private var _binding: DialogCalendarBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: LocalDate? = null
    private var onDateSelected: ((LocalDate) -> Unit)? = null
    private var currentMonth: YearMonth = YearMonth.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendar()
        setupClickListeners()
    }

    private fun setupCalendar() {
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
            lateinit var day: CalendarDay

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        // Notify about the previous selection being cleared
                        selectedDate?.let { oldDate ->
                            binding.calendarView.notifyDateChanged(oldDate)
                        }
                        // Update the new selection
                        selectedDate = day.date
                        binding.calendarView.notifyDateChanged(day.date)
                        binding.btnConfirm.isEnabled = true
                    }
                }
            }
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()

                if (day.position == DayPosition.MonthDate) {
                    textView.visibility = View.VISIBLE
                    if (day.date == selectedDate) {
                        textView.setBackgroundResource(R.drawable.calendar_day_selected_background)
                        textView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                    } else {
                        textView.background = null
                        textView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                    }
                } else {
                    textView.visibility = View.INVISIBLE
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = CalendarHeaderLayoutBinding.bind(view).headerTextView
            val btnPreviousMonth = CalendarHeaderLayoutBinding.bind(view).btnPreviousMonth
            val btnNextMonth = CalendarHeaderLayoutBinding.bind(view).btnNextMonth
        }

        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = month.yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                
                container.btnPreviousMonth.setOnClickListener {
                    currentMonth = currentMonth.minusMonths(1)
                    binding.calendarView.findFirstVisibleMonth()?.let { visibleMonth ->
                        if (visibleMonth.yearMonth > currentMonth.minusMonths(10)) {
                            binding.calendarView.smoothScrollToMonth(currentMonth)
                        }
                    }
                }

                container.btnNextMonth.setOnClickListener {
                    currentMonth = currentMonth.plusMonths(1)
                    binding.calendarView.findFirstVisibleMonth()?.let { visibleMonth ->
                        if (visibleMonth.yearMonth < currentMonth.plusMonths(10)) {
                            binding.calendarView.smoothScrollToMonth(currentMonth)
                        }
                    }
                }
            }
        }

        // Add scroll listener to update current month
        binding.calendarView.monthScrollListener = { month ->
            currentMonth = month.yearMonth
        }
    }

    private fun setupClickListeners() {
        binding.btnConfirm.isEnabled = false
        binding.btnConfirm.setOnClickListener {
            selectedDate?.let { date ->
                onDateSelected?.invoke(date)
                dismiss()
            }
        }
    }

    fun setOnDateSelectedListener(listener: (LocalDate) -> Unit) {
        onDateSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CalendarDialogFragment()
    }
} 