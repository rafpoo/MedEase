package com.example.medease.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.R
import com.example.medease.adapter.ManageAppointmentAdapter
import com.example.medease.databinding.FragmentManageAppointmentBinding
import com.example.medease.repository.AppointmentRepository

class ManageAppointmentFragment : Fragment() {

    private var _binding: FragmentManageAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ManageAppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 🔹 Ambil data dari repository
        val appointments = AppointmentRepository.getAppointments()

        // 🔹 Setup RecyclerView
        adapter = ManageAppointmentAdapter(
            appointments,
            onEdit = { appointment ->
                // Navigasi ke fragment edit (bisa kamu buat nanti)
                val action = ManageAppointmentFragmentDirections
                    .actionManageAppointmentFragmentToEditAppointmentFragment(appointment.id.toString())
                findNavController().navigate(action)

            },
            onDelete = { appointment ->
                AppointmentRepository.deleteAppointment(appointment.id)
                adapter.updateData(AppointmentRepository.getAppointments())
                Toast.makeText(requireContext(), "Appointment deleted", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvManageAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvManageAppointments.adapter = adapter

        binding.tvNoData.visibility =
            if (appointments.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}