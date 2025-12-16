package com.example.medease.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.medease.R
import com.example.medease.data.model.Doctor
import com.example.medease.data.model.Schedule
import com.example.medease.databinding.FragmentAddEditDoctorBinding
import com.example.medease.database.viewModels.DoctorViewModel
import com.example.medease.utils.showConfirmDialog
import com.google.android.material.chip.Chip

class EditDoctorFragment : Fragment(R.layout.fragment_add_edit_doctor) {

    private lateinit var binding: FragmentAddEditDoctorBinding
    private lateinit var viewModel: DoctorViewModel
    private val schedules = mutableListOf<Schedule>()
    private lateinit var currentDoctor: Doctor

    private val doctorId: String by lazy {
        arguments?.getString("doctor_id")
            ?: throw IllegalArgumentException("doctor_id is required")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddEditDoctorBinding.bind(view)
        viewModel = ViewModelProvider(this)[DoctorViewModel::class.java]

        setupDaySpinner()
        loadDoctor()
        setupScheduleChip()

        binding.btnSave.setOnClickListener {
            showConfirmDialog(requireActivity(), "Are you sure?") {
                updateDoctor()
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

    private fun loadDoctor() {
        viewModel.doctors.observe(viewLifecycleOwner) { list ->
            val doctor = list.find { it.id == doctorId }

            if (doctor == null) {
                Toast.makeText(requireContext(), "Doctor not found", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
                return@observe
            }

            currentDoctor = doctor

            binding.etName.setText(doctor.name)
            binding.etCategory.setText(doctor.category)
            binding.etDescription.setText(doctor.description)

            schedules.clear()
            binding.chipGroupSchedules.removeAllViews()

            schedules.addAll(doctor.schedules)
            schedules.forEach { addChip(it) }

            // set spinner ke hari pertama jadwal
            doctor.schedules.firstOrNull()?.let {
                binding.spinnerDay.setText(it.day, false)
            }
        }

        viewModel.loadDoctors()
    }

    private fun setupScheduleChip() {
        binding.btnAddSchedule.setOnClickListener {

            val day = binding.spinnerDay.text.toString()
            val time = binding.etSchedule.text.toString().trim()

            if (day.isBlank() || time.isBlank()) {
                Toast.makeText(requireContext(), "Day and time required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val schedule = Schedule(day, time)

            if (schedules.any { it.day == day && it.time == time }) {
                Toast.makeText(requireContext(), "Schedule already exists", Toast.LENGTH_SHORT).show()
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

    private fun updateDoctor() {

        val updated = currentDoctor.copy(
            name = binding.etName.text.toString().trim(),
            category = binding.etCategory.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            schedules = schedules
        )

        viewModel.updateDoctor(updated) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Doctor updated", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

