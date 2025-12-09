package com.example.medease.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.medease.databinding.FragmentProfileBinding
import com.example.medease.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val storage = Firebase.storage

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
            val name = binding.edtName.text.toString()
            val phone = binding.edtPhone.text.toString()

            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            db.collection("users").document(userId)
                .update(
                    mapOf(
                        "name" to name,
                        "phone" to phone
                    )
                )
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
                    binding.edtName.setText(doc.getString("name"))
                    binding.edtEmail.setText(doc.getString("email"))
                    binding.edtPhone.setText(doc.getString("phone"))

                    val photoUrl = doc.getString("photoUrl")
                    if (!photoUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(photoUrl)
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
                val uri = data?.data
                binding.imgProfile.setImageURI(uri)
                if (uri != null) uploadPhoto(uri, userId)
            }

            RC_CAMERA -> {
                val bmp = data?.extras?.get("data") as Bitmap
                binding.imgProfile.setImageBitmap(bmp)

                val baos = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                uploadByteArray(baos.toByteArray(), userId)
            }
        }
    }

    private fun uploadPhoto(uri: Uri, userId: String) {
        val ref = storage.reference.child("profile/$userId.jpg")

        ref.putFile(uri)
            .continueWithTask { ref.downloadUrl }
            .addOnSuccessListener { url ->
                savePhotoUrl(url.toString(), userId)
            }
    }

    private fun uploadByteArray(bytes: ByteArray, userId: String) {
        val ref = storage.reference.child("profile/$userId.jpg")

        ref.putBytes(bytes)
            .continueWithTask { ref.downloadUrl }
            .addOnSuccessListener { url ->
                savePhotoUrl(url.toString(), userId)
            }
    }

    private fun savePhotoUrl(url: String, userId: String) {
        db.collection("users").document(userId)
            .update("photoUrl", url)
    }
}
