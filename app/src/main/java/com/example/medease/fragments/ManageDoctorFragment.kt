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
                Toast.makeText(requireContext(), "Edit ${doctor.name}", Toast.LENGTH_SHORT).show()
                // nanti bisa buka EditDoctorFragment
            },
            onDelete = { doctor ->
                viewModel.deleteDoctor(doctor.id) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Doctor deleted", Toast.LENGTH_SHORT).show()
                        viewModel.loadDoctors()
                    }
                }
            }
        )

        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = adapter

        viewModel.doctors.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.tvNoData.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loadDoctors()
    }
}
