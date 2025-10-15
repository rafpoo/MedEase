package com.example.medease

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.medease.databinding.ActivityDoctorDashboardBinding
import com.example.medease.fragments.DoctorAppointmentsFragment
import com.example.medease.fragments.DoctorCalendarFragment
import com.example.medease.fragments.DoctorConsultationsFragment
import com.example.medease.fragments.DoctorMailboxFragment
import com.example.medease.fragments.DoctorProfileFragment
import com.example.medease.fragments.DoctorScheduleFragment


class DoctorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Default tampilan: dashboard utama (home) ---
        showDashboard()

        // --- Handle tombol back gesture ---
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                    showDashboard()
                } else {
                    finish()
                }
            }
        })

        // --- Handle Bottom Navigation ---
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showDashboard()
                    true
                }
                R.id.nav_mailbox -> {
                    openFragmentFullScreen(DoctorMailboxFragment())
                    true
                }
                R.id.nav_calendar -> {
                    openFragmentFullScreen(DoctorCalendarFragment())
                    true
                }
                R.id.nav_profile -> {
                    openFragmentFullScreen(DoctorProfileFragment())
                    true
                }
                else -> false
            }
        }

        // --- Klik Card di Dashboard ---
        binding.cardViewSchedule.setOnClickListener {
            openFragmentFullScreen(DoctorScheduleFragment())
        }
        binding.cardConsultation.setOnClickListener {
            openFragmentFullScreen(DoctorConsultationsFragment())
        }
        binding.cardRequests.setOnClickListener {
            openFragmentFullScreen(DoctorAppointmentsFragment())
        }
        binding.cardProfile.setOnClickListener {
            openFragmentFullScreen(DoctorProfileFragment())
        }
    }

    private fun openFragmentFullScreen(fragment: Fragment) {
        // Sembunyikan dashboard utama
        binding.scrollViewDashboard.visibility = View.GONE

        // Pastikan FrameLayout muncul
        binding.fragmentContainer.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showDashboard() {
        // Tampilkan dashboard, sembunyikan fragment
        binding.scrollViewDashboard.visibility = View.VISIBLE
        binding.fragmentContainer.visibility = View.GONE

        // Bersihkan fragment stack
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
