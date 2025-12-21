package com.example.medease.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.medease.databinding.FragmentProfileBinding
import com.example.medease.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val RC_GALLERY = 100
    private val RC_CAMERA = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserData()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.btnChangePhoto.setOnClickListener {
            showPhotoPicker()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val phone = binding.edtPhone.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            db.collection("users").document(userId)
                .update(
                    mapOf(
                        "nama" to name,
                        "noHp" to phone
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal update profil", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    binding.edtName.setText(doc.getString("nama") ?: "")
                    binding.edtEmail.setText(doc.getString("email") ?: "")
                    binding.edtPhone.setText(doc.getString("noHp") ?: "")

                    val base64 = doc.getString("picture")
                    if (!base64.isNullOrEmpty()) {
                        val bytes = Base64.decode(base64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                        Glide.with(requireContext())
                            .load(bitmap)
                            .circleCrop()
                            .into(binding.imgProfile)
                    }
                }
            }
    }

    private fun showPhotoPicker() {
        val options = arrayOf("Kamera", "Galeri")

        AlertDialog.Builder(requireContext())
            .setTitle("Ganti Foto Profil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RC_GALLERY)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, RC_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        val userId = auth.currentUser?.uid ?: return

        when (requestCode) {
            RC_GALLERY -> {
                val uri = data?.data ?: return
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                saveBitmapToFirestore(bitmap, userId)
            }

            RC_CAMERA -> {
                val bitmap = data?.extras?.get("data") as? Bitmap ?: return
                saveBitmapToFirestore(bitmap, userId)
            }
        }
    }

    // ================= BASE64 CORE =================

    private fun saveBitmapToFirestore(bitmap: Bitmap, userId: String) {
        val resized = Bitmap.createScaledBitmap(bitmap, 256, 256, true)

        val baos = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

        db.collection("users").document(userId)
            .set(mapOf("picture" to base64), SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Foto profil diperbarui", Toast.LENGTH_SHORT).show()
                binding.imgProfile.setImageBitmap(resized)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
            }
    }
}
