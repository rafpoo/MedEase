package com.example.medease.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.medease.R
import com.example.medease.databinding.ActivityAdminDashboardBinding
import com.example.medease.fragments.AppointmentsFragment
import com.example.medease.fragments.CalendarFragment
import com.example.medease.fragments.ManageDoctorFragment
import com.example.medease.fragments.ScheduleFragment
import com.example.medease.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth


class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
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

                R.id.nav_calendar -> {
                    openFragmentFullScreen(CalendarFragment())
                    true
                }

                else -> false
            }
        }

        // --- Klik Card di Dashboard ---
        binding.cardViewSchedule.setOnClickListener {
            openFragmentFullScreen(ScheduleFragment())
        }

        binding.cardRequests.setOnClickListener {
            openFragmentFullScreen(AppointmentsFragment())
        }
        binding.btnLogoutAdmin.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }



        binding.cardLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.cardManageDoctor.setOnClickListener {
            binding.scrollViewDashboard.visibility = View.GONE
            binding.fragmentContainer.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageDoctorFragment())
                .addToBackStack(null)
                .commit()
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
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}