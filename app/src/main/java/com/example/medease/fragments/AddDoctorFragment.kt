package com.example.medease.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.medease.R
import com.example.medease.data.model.Doctor
import com.example.medease.databinding.FragmentAddEditDoctorBinding
import com.example.medease.database.viewModels.DoctorViewModel
import com.google.android.material.chip.Chip

class AddDoctorFragment : Fragment(R.layout.fragment_add_edit_doctor) {

    private lateinit var binding: FragmentAddEditDoctorBinding
    private lateinit var viewModel: DoctorViewModel
    private val schedules = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddEditDoctorBinding.bind(view)
        viewModel = ViewModelProvider(this)[DoctorViewModel::class.java]

        setupScheduleChip()

        binding.btnSave.setOnClickListener {
            saveDoctor()
        }
    }

    private fun setupScheduleChip() {
        binding.btnAddSchedule.setOnClickListener {
            val time = binding.etSchedule.text.toString().trim()

            if (time.isEmpty()) {
                binding.etSchedule.error = "Time required"
                return@setOnClickListener
            }

            if (time in schedules) return@setOnClickListener

            schedules.add(time)

            val chip = Chip(requireContext()).apply {
                text = time
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    schedules.remove(text.toString())
                    binding.chipGroupSchedules.removeView(this)
                }
            }

            binding.chipGroupSchedules.addView(chip)
            binding.etSchedule.text?.clear()
        }
    }

    private fun saveDoctor() {
        val name = binding.etName.text.toString()
        val category = binding.etCategory.text.toString()
        val desc = binding.etDescription.text.toString()

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
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Failed to add doctor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
