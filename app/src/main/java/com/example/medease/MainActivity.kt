package com.example.medease

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.medease.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    private val viewModel by lazy {
        ViewModelProvider(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tombol untuk menuju halaman pemesanan obat
//        val btnOrderMeds: Button = findViewById(R.id.btnOrderMeds)
//        btnOrderMeds.setOnClickListener {
//            val intent = Intent(this, OrderMeds::class.java)
//            startActivity(intent)
//        }
//        val btnMakeApt: Button = findViewById(R.id.btnMakeApt)
//        btnMakeApt.setOnClickListener {
//            val intent = Intent(this, MakeAppointment::class.java)
//            startActivity(intent)
//        }
        // 🔹 Temukan NavHostFragment dari layout
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 🔹 Hubungkan toolbar dengan NavController
        val toolbar = findViewById<MaterialToolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar) // penting agar navigation icon bisa tampil otomatis

        // 🔹 Setup toolbar supaya ikut navigasi (tampil title & tombol back otomatis)
        setupActionBarWithNavController(navController)

        // Jika kamu ingin bottom nav / drawer, baru gunakan:
        // toolbar.setupWithNavController(navController)
        // tapi untuk toolbar saja, setupActionBarWithNavController lebih tepat
    }

    override fun onSupportNavigateUp(): Boolean {
        // 🔙 Handle tombol back (arrow di toolbar)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
