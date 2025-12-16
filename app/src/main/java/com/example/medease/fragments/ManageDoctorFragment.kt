package com.example.medease.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.R
import com.example.medease.adapter.AdminDoctorAdapter
import com.example.medease.database.viewModels.DoctorViewModel
import com.example.medease.databinding.FragmentManageDoctorBinding
import com.example.medease.utils.showConfirmDialog

class ManageDoctorFragment : Fragment(R.layout.fragment_manage_doctor) {

    private lateinit var binding: FragmentManageDoctorBinding
    private lateinit var viewModel: DoctorViewModel
    private lateinit var adapter: AdminDoctorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentManageDoctorBinding.bind(view)
        viewModel = ViewModelProvider(this)[DoctorViewModel::class.java]


        adapter = AdminDoctorAdapter(
            onEdit = { doctor ->
                val fragment = EditDoctorFragment().apply {
                    arguments = Bundle().apply {
                        putString("doctor_id", doctor.id)
                    }
                }

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDelete = { doctor ->
                showConfirmDialog(
                    requireActivity(),
                    "Are you sure you want to delete this doctor?"
                ) {
                    viewModel.deleteDoctor(doctor.id) { success ->
                        if (success) {
                            Toast.makeText(requireContext(), "Doctor deleted", Toast.LENGTH_SHORT).show()
                            viewModel.loadDoctors()
                        }
                    }
                }

            }
        )

        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = adapter
        binding.fabAddDoctor.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddDoctorFragment())
                .addToBackStack(null)
                .commit()
        }

        viewModel.doctors.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.tvNoData.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loadDoctors()
    }
}
