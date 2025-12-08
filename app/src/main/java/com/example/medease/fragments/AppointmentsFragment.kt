package com.example.medease.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.adapter.AdminAppointmentAdapter
import com.example.medease.data.model.Appointment
import com.example.medease.database.viewModels.AppointmentViewModel
import com.example.medease.databinding.FragmentDoctorAppointmentsBinding

class AppointmentsFragment : Fragment() {

    private var _binding: FragmentDoctorAppointmentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AppointmentViewModel
    private lateinit var adapter: AdminAppointmentAdapter
    // cache semua appointment dari ViewModel
    private var allAppointmentsCache: List<Appointment> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDoctorAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AppointmentViewModel::class.java]

        adapter = AdminAppointmentAdapter(
            onAccept = { appointment ->
                viewModel.acceptAppointment(appointment.id) { success ->
                    if (success) Toast.makeText(requireContext(), "Appointment accepted", Toast.LENGTH_SHORT).show()
                }
            },
            onDecline = { appointment ->
                viewModel.declineAppointment(appointment.id) { success ->
                    if (success) Toast.makeText(requireContext(), "Appointment declined", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.rvAdminAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminAppointments.adapter = adapter

        // Observer: update cache, dan jika fragment sudah visible/resumed -> refresh UI
        viewModel.allAppointments.observe(viewLifecycleOwner) { list ->
            allAppointmentsCache = list ?: emptyList()

            // Jika fragment sedang minimal STARTED/RESUMED, refresh pending list
            if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                refreshPendingList()
            }
            // jika belum STARTED, onResume() akan memanggil refreshPendingList()
        }
    }

    override fun onResume() {
        super.onResume()
        // selalu refresh saat fragment benar-benar terlihat
        refreshPendingList()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadAllAppointments()
    }

    private fun refreshPendingList() {
        val pendingOnly = allAppointmentsCache.filter { it.status == "pending" }

        // submit pending list — ini akan membuat card yang sudah di-accept/decline tetap terlihat
        // sampai user keluar dan masuk kembali (karena kita hanya submit pending saat fragment dibuka)
        adapter.submitList(pendingOnly)

        // empty message handling
        binding.tvEmptyMessage.visibility = if (pendingOnly.isEmpty()) View.VISIBLE else View.GONE
        binding.rvAdminAppointments.visibility = if (pendingOnly.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
