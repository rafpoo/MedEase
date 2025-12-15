package com.example.medease.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.medease.R
import com.example.medease.data.model.Doctor
import com.example.medease.databinding.FragmentAddEditDoctorBinding
import com.example.medease.database.viewModels.DoctorViewModel
import com.google.android.material.chip.Chip

class EditDoctorFragment : Fragment(R.layout.fragment_add_edit_doctor) {

    private val args: EditDoctorFragmentArgs by navArgs()
    private lateinit var binding: FragmentAddEditDoctorBinding
    private lateinit var viewModel: DoctorViewModel
    private val schedules = mutableListOf<String>()
    private lateinit var currentDoctor: Doctor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddEditDoctorBinding.bind(view)
        viewModel = ViewModelProvider(this)[DoctorViewModel::class.java]

        loadDoctor()
        setupScheduleChip()

        binding.btnSave.setOnClickListener {
            updateDoctor()
        }
    }

    private fun loadDoctor() {
        viewModel.doctors.observe(viewLifecycleOwner) { list ->
            currentDoctor = list.first { it.id == args.doctorId }

            binding.etName.setText(currentDoctor.name)
            binding.etCategory.setText(currentDoctor.category)
            binding.etDescription.setText(currentDoctor.description)

            schedules.clear()
            schedules.addAll(currentDoctor.schedules)

            schedules.forEach { time ->
                addChip(time)
            }
        }

        viewModel.loadDoctors()
    }

    private fun setupScheduleChip() {
        binding.btnAddSchedule.setOnClickListener {
            val time = binding.etSchedule.text.toString().trim()
            if (time.isNotEmpty() && time !in schedules) {
                schedules.add(time)
                addChip(time)
                binding.etSchedule.text?.clear()
            }
        }
    }

    private fun addChip(time: String) {
        val chip = Chip(requireContext()).apply {
            text = time
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                schedules.remove(text.toString())
                binding.chipGroupSchedules.removeView(this)
            }
        }
        binding.chipGroupSchedules.addView(chip)
    }

    private fun updateDoctor() {
        val updated = currentDoctor.copy(
            name = binding.etName.text.toString(),
            category = binding.etCategory.text.toString(),
            description = binding.etDescription.text.toString(),
            schedules = schedules
        )

        viewModel.updateDoctor(updated) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Doctor updated", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
