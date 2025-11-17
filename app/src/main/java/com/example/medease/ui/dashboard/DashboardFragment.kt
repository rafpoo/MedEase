package com.example.medease.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.R
import com.example.medease.adapter.AppointmentAdapter
import com.example.medease.database.AppointmentViewModel
import com.example.medease.database.TotalDatabase
import com.example.medease.databinding.FragmentDashboardBinding
import com.example.medease.repository.AppointmentRepository
import com.example.medease.ui.login.LoginActivity
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AppointmentViewModel
    private lateinit var adapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // INIT VIEWMODEL
        val dao = TotalDatabase.getInstance(requireContext()).appointmentDao()
        val repo = AppointmentRepository(dao)
        viewModel = AppointmentViewModel(repo)

        // SETUP RECYCLER VIEW
        adapter = AppointmentAdapter(emptyList())
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter

        // OBSERVE VIEWMODEL
        viewModel.appointments.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            binding.tvNoAppointments.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // LOAD DATA
        viewModel.loadAll()

        // Navigasi
        binding.cardBeliObat.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_orderMedsFragment)
        }
        binding.cardBuatJanji.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_makeAppointmentFragment)
        }
        binding.cardLihatKonsultasi.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_manageAppointmentFragment)
        }
        binding.cardLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.cardKonsulOnline.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_KonsulOnlineFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

