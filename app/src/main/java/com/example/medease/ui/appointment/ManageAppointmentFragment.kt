package com.example.medease.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.adapter.ManageAppointmentAdapter
import com.example.medease.database.AppointmentViewModel
import com.example.medease.database.AppointmentViewModelFactory
import com.example.medease.database.TotalDatabase
import com.example.medease.databinding.FragmentManageAppointmentBinding
import com.example.medease.repository.AppointmentRepository

class ManageAppointmentFragment : Fragment() {

    private var _binding: FragmentManageAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AppointmentViewModel
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

        // 🔹 Init ViewModel
        val appContext = requireContext().applicationContext
        val db = TotalDatabase.getInstance(appContext)
        val repo = AppointmentRepository(db.appointmentDao())
        val factory = AppointmentViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[AppointmentViewModel::class.java]

        // 🔹 Setup RecyclerView
        adapter = ManageAppointmentAdapter(
            emptyList(),
            onEdit = { appointment ->
                val action =
                    ManageAppointmentFragmentDirections
                        .actionManageAppointmentFragmentToEditAppointmentFragment(appointment.id)
                findNavController().navigate(action)
            },
            onDelete = { appointment ->
                viewModel.delete(appointment)
                Toast.makeText(requireContext(), "Appointment deleted", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvManageAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvManageAppointments.adapter = adapter

        // 🔹 Observe data dari ViewModel
        viewModel.appointments.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            binding.tvNoData.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // 🔹 Load data pertama
        viewModel.loadAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
