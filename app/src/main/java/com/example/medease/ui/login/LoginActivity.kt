package com.example.medease.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medease.ui.doctor.DoctorDashboardActivity
import com.example.medease.MainActivity
import com.example.medease.R

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        val dummyAccounts = mapOf(
            "doctor" to "12345",
            "user" to "12345"
        )

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val correctPassword = dummyAccounts[username]
            if (correctPassword != null && password == correctPassword) {
                Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

                // 🔹 Logic berdasarkan role
                val intent = when (username) {
                    "doctor" -> Intent(this, DoctorDashboardActivity::class.java)
                    "user" -> Intent(this, MainActivity::class.java)
                    else -> null
                }

                intent?.let {
                    it.putExtra("username", username)
                    startActivity(it)
                    finish()
                }
            } else {
                Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            Toast.makeText(this, "Fitur register belum tersedia", Toast.LENGTH_SHORT).show()
        }
    }
}
