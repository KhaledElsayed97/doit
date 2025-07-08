package dev.khaled.doit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.khaled.doit.R
import dev.khaled.doit.databinding.FragmentHomeBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeSelectedDate()
    }

    private fun setupClickListeners() {
        binding.mcvDaily.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_DailyTasksFragment)
        }
        
        binding.mcvOneTimeTasks.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_OneTimeTasksFragment)
        }

        binding.ivProfile.setOnClickListener {
            (activity as? HomeActivity)?.openDrawer()
        }

        binding.btnCalendar.setOnClickListener {
            showCalendarDialog()
        }
    }

    private fun observeSelectedDate() {
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            if(date == LocalDate.now()){
                binding.tvDate.text = getString(R.string.home_date,viewModel.currentUser.value?.name, "today")
                binding.tvOneTime.text = getString(R.string.home_one_time_date,"today")
            }
            else{
                val formattedDate = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                binding.tvDate.text = getString(R.string.home_date, formattedDate.toString())
                binding.tvOneTime.text = getString(R.string.home_one_time_date,formattedDate.toString())
            }

        }
    }

    private fun showCalendarDialog() {
        val calendarDialog = CalendarDialogFragment.newInstance()
        calendarDialog.setOnDateSelectedListener { date ->
            viewModel.setSelectedDate(date)
        }
        calendarDialog.show(childFragmentManager, "CalendarDialog")
    }
}