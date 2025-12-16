package com.example.medease.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medease.R
import com.example.medease.data.model.Doctor
import com.example.medease.data.model.Schedule
import com.example.medease.databinding.FragmentAddEditDoctorBinding
import com.example.medease.database.viewModels.DoctorViewModel
import com.example.medease.utils.showConfirmDialog
import com.google.android.material.chip.Chip

class AddDoctorFragment : Fragment(R.layout.fragment_add_edit_doctor) {

    private lateinit var binding: FragmentAddEditDoctorBinding
    private lateinit var viewModel: DoctorViewModel
    private val schedules = mutableListOf<Schedule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddEditDoctorBinding.bind(view)
        viewModel = ViewModelProvider(this)[DoctorViewModel::class.java]

        setupDaySpinner()
        setupScheduleChip()

        binding.btnSave.setOnClickListener {
            showConfirmDialog(requireActivity(), "Are you sure?") {
                saveDoctor()
            }
        }
    }

    private fun setupDaySpinner() {
        val days = listOf(
            "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            days
        )

        binding.spinnerDay.setAdapter(adapter)

        // 🔥 PENTING: set default value
        binding.spinnerDay.setText(days.first(), false)
    }


    private fun setupScheduleChip() {
        binding.btnAddSchedule.setOnClickListener {

            val day = binding.spinnerDay.text.toString()
            val time = binding.etSchedule.text.toString().trim()

            if (day.isBlank() || time.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Day and time are required",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val schedule = Schedule(day = day, time = time)

            if (schedules.any { it.day == day && it.time == time }) {
                Toast.makeText(requireContext(), "Schedule already added", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            schedules.add(schedule)
            addChip(schedule)

            binding.etSchedule.text?.clear()
        }
    }

    private fun addChip(schedule: Schedule) {
        val chip = Chip(requireContext()).apply {
            text = "${schedule.day} • ${schedule.time}"
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                schedules.remove(schedule)
                binding.chipGroupSchedules.removeView(this)
            }
        }
        binding.chipGroupSchedules.addView(chip)
    }

    private fun saveDoctor() {
        val name = binding.etName.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()

        if (name.isBlank() || category.isBlank() || schedules.isEmpty()) {
            Toast.makeText(requireContext(), "Complete all fields", Toast.LENGTH_SHORT).show()
            return
        }



        val doctor = Doctor(
            name = name,
            category = category,
            description = desc,
            schedules = schedules
        )

        viewModel.addDoctor(doctor) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Doctor added", Toast.LENGTH_SHORT).show()
                requireActivity()
                    .supportFragmentManager
                    .popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to add doctor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

