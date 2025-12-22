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
import com.example.medease.database.viewModels.AppointmentViewModel
import com.example.medease.database.viewModels.AppointmentViewModelFactory
import com.example.medease.database.repositories.AppointmentRepository
import com.example.medease.databinding.FragmentManageAppointmentBinding
import com.google.firebase.auth.FirebaseAuth

class ManageAppointmentFragment : Fragment() {

    private var _binding: FragmentManageAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AppointmentViewModel
    private lateinit var adapter: ManageAppointmentAdapter
    private lateinit var auth: FirebaseAuth

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
        viewModel = ViewModelProvider(this)[AppointmentViewModel::class.java]

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
                viewModel.deleteAppointment(appointment) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Appointment berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        binding.rvManageAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvManageAppointments.adapter = adapter

        // 🔹 Observe data dari ViewModel
        viewModel.userAppointments.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            binding.tvNoData.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // 🔹 Load data pertama
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: ""

        if (userId.isNotEmpty()) {
            viewModel.loadAppointments(userId)
        } else {
            Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
