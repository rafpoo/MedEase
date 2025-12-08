package com.example.medease.ui.profile

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.medease.databinding.ProfileFragmentBinding
import com.example.medease.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFragmentBinding
    private var imageUri: Uri? = null

    /** =============================
     *  PERMISSION REQUEST CAMERA
     *  ============================= */
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera()
            else Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }

    /** =============================
     *  PICK FROM GALLERY
     *  ============================= */
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.imgProfile.setImageURI(uri)
        }
    }

    /** =============================
     *  TAKE PHOTO FROM CAMERA
     *  ============================= */
    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            binding.imgProfile.setImageURI(imageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)

        binding.btnEditPhoto.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }


        return binding.root
    }

    private fun logoutUser() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    /** =============================
     *  DIALOG PILIH SUMBER FOTO
     *  ============================= */
    private fun showImagePickerDialog() {
        val options = arrayOf("Ambil Foto", "Pilih dari Galeri")

        AlertDialog.Builder(requireContext())
            .setTitle("Ubah Foto Profil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission.launch(Manifest.permission.CAMERA)
                    1 -> openGallery()
                }
            }
            .show()
    }

    /** =============================
     *  OPEN GALLERY
     *  ============================= */
    private fun openGallery() {
        pickImage.launch("image/*")
    }

    /** =============================
     *  OPEN CAMERA (SETELAH IZIN OK)
     *  ============================= */
    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }

        imageUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        takePhoto.launch(imageUri)
    }
}
