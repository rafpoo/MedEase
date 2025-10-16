package com.example.medease

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tombol untuk menuju halaman pemesanan obat
        val btnOrderMeds: Button = findViewById(R.id.btnOrderMeds)
        btnOrderMeds.setOnClickListener {
            val intent = Intent(this, OrderMeds::class.java)
            startActivity(intent)
        }
        val btnMakeApt: Button = findViewById(R.id.btnMakeApt)
        btnMakeApt.setOnClickListener {
            val intent = Intent(this, MakeAppointment::class.java)
            startActivity(intent)
        }
    }
}
