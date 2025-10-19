package com.example.medease.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.adapter.AppointmentAdapter
import com.example.medease.databinding.FragmentDashboardBinding
import com.example.medease.repository.AppointmentRepository
import com.example.medease.ui.login.LoginActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
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

        // 🔹 Navigasi ke Order Obat
        binding.cardBeliObat.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_orderMedsFragment)
        }

        // 🔹 Navigasi ke Buat Janji
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

        // 🔹 Load data dari repository
        val list = AppointmentRepository.getAppointments()

        // 🔹 Setup RecyclerView
        adapter = AppointmentAdapter(list)
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter

        // 🔹 Tampilkan pesan jika belum ada data
        binding.tvNoAppointments.visibility =
            if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}