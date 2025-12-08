package com.example.medease.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.medease.MainActivity
import com.example.medease.R
import com.example.medease.data.model.User
import com.example.medease.database.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class RegisterActivity: AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var fullName: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private lateinit var backToLoginButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        email = findViewById<EditText>(R.id.etRegisterUsername)
        password = findViewById<EditText>(R.id.etRegisterPassword)
        confirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        fullName = findViewById<EditText>(R.id.etFullName)
        etPhoneNumber = findViewById<EditText>(R.id.etPhoneNumber)
        backToLoginButton = findViewById<Button>(R.id.btnBackToLogin)

        auth = Firebase.auth

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.loadCurrentUser()
//        userViewModel.currentUser.observe(this) { user ->
//            if (user != null) {
//
//            }
//        }

        registerButton = findViewById<Button>(R.id.btnSubmitRegister)
        registerButton.setOnClickListener {
            val txtEmail = email.text.toString()
            val txtPassword = password.text.toString()
            val txtConfirmPassword = confirmPassword.text.toString()
            val txtFullName = fullName.text.toString()
            val txtPhoneNumber = etPhoneNumber.text.toString()


            if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show()
            } else if (txtPassword.length <= 6) {
                Toast.makeText(this, "Password harus lebih dari 6 karakter", Toast.LENGTH_SHORT).show()
            } else if (txtPassword != txtConfirmPassword) {
                Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_SHORT).show()
            } else {
                val newUser = User(
                    nama = txtFullName,
                    noHp = txtPhoneNumber,
                    email = txtEmail
                )
                registerUser(txtEmail, txtPassword, newUser)
            }
        }

        backToLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun registerUser(email: String, password: String, newUserProps: User) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val newUser = newUserProps.copy(id = uid)

                    userViewModel.addUser(newUser) { success ->
                        if (success) {
                            Log.d("RegisterActivity", "User berhasil disimpan ke Firestore")
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Gagal menyimpan user ke Firestore", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}