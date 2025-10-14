package com.example.medease

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medease.databinding.ActivityDoctorDashboardBinding

class DoctorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Klik menu Jadwal
        binding.cardViewSchedule.setOnClickListener {
            Toast.makeText(this, "Buka Jadwal Dokter", Toast.LENGTH_SHORT).show()
            // Contoh: startActivity(Intent(this, DoctorScheduleActivity::class.java))
        }

        // Klik menu Konsultasi
        binding.cardConsultation.setOnClickListener {
            Toast.makeText(this, "Buka Konsultasi", Toast.LENGTH_SHORT).show()
        }

        // Klik menu Permintaan
        binding.cardRequests.setOnClickListener {
            Toast.makeText(this, "Buka Reservasi Pasien", Toast.LENGTH_SHORT).show()
        }

        // Klik menu Profil
        binding.cardProfile.setOnClickListener {
            Toast.makeText(this, "Buka Profil Dokter", Toast.LENGTH_SHORT).show()
        }

    }
}
