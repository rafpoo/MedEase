package com.example.medease.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.R
import com.example.medease.adapter.AppointmentAdapter
import com.example.medease.database.viewModels.AppointmentViewModel
import com.example.medease.database.viewModels.UserViewModel
import com.example.medease.databinding.FragmentDashboardBinding
import com.example.medease.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: AppointmentAdapter

    private lateinit var txtNamaUser: TextView

    private lateinit var auth: FirebaseAuth

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
        appointmentViewModel = ViewModelProvider(this)
            .get(AppointmentViewModel::class.java)
        userViewModel = ViewModelProvider(this)
            .get(UserViewModel::class.java)

        // SETUP RECYCLER VIEW
        adapter = AppointmentAdapter(emptyList())
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter

        // OBSERVE VIEWMODEL
        appointmentViewModel.userAppointments.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            binding.tvNoAppointments.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // LOAD DATA
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: ""

        // display nama
        txtNamaUser = binding.txtNamaUser
        userViewModel.loadCurrentUser()
        appointmentViewModel.loadAppointments(userId)

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("DashboardFragment", "Nama user: ${user.nama}")
                txtNamaUser.text = user.nama
            }
        }

        // Navigasi yang DIPERTAHANKAN
        binding.cardBeliObat.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_orderMedsFragment)
        }
        binding.cardBuatJanji.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_makeAppointmentFragment)
        }
        binding.cardLihatKonsultasi.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_manageAppointmentFragment)
        }

        binding.cardProfil.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
        }

        binding.cardPrediksiWQI.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_wqiFragment)
        }


        // ❌ cardKonsulOnline → DIHAPUS
        // ❌ cardLogout → DIHAPUS
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
