package com.example.medease.ui.dashboard

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.medease.R
import com.example.medease.adapter.AppointmentAdapter
import com.example.medease.database.viewModels.AppointmentViewModel
import com.example.medease.database.viewModels.UserViewModel
import com.example.medease.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: AppointmentAdapter
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

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return

        // INIT VIEWMODEL
        appointmentViewModel = ViewModelProvider(this)[AppointmentViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // SETUP RECYCLER
        adapter = AppointmentAdapter(emptyList())
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter

        // OBSERVE APPOINTMENTS
        appointmentViewModel.userAppointments.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            binding.tvNoAppointments.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // LOAD DATA
        userViewModel.loadCurrentUser()
        appointmentViewModel.loadAppointments(userId)

        // ================= USER OBSERVER (NAMA + FOTO) =================
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {

                // Nama user
                binding.txtNamaUser.text = user.nama
                Log.d("DashboardFragment", "Nama user: ${user.nama}")

                // Foto profil (Base64 → ImageView profileImage)
                val base64 = user.picture
                if (!base64.isNullOrEmpty()) {
                    try {
                        val bytes = Base64.decode(base64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                        Glide.with(requireContext())
                            .load(bitmap)
                            .circleCrop()
                            .into(binding.profileImage)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("DashboardFragment", "Gagal decode foto Base64")
                    }
                }
            }
        }

        // ================= NAVIGATION =================
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
