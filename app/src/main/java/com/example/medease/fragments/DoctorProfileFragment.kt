package com.example.medease.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.medease.R

class DoctorProfileFragment : Fragment() {

    private lateinit var etFullName: EditText
    private lateinit var etEducation: EditText
    private lateinit var etSpecialization: EditText
    private lateinit var etSchedule: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    private lateinit var sharedPrefs: android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_profile, container, false)

        etFullName = view.findViewById(R.id.et_full_name)
        etEducation = view.findViewById(R.id.et_education)
        etSpecialization = view.findViewById(R.id.et_specialization)
        etSchedule = view.findViewById(R.id.et_schedule)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        btnLogout = view.findViewById(R.id.btn_logout)

        sharedPrefs = requireContext().getSharedPreferences("DoctorProfile", Context.MODE_PRIVATE)

        loadProfileData()

        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Anda telah logout.", Toast.LENGTH_SHORT).show()
            // TODO: arahkan ke halaman login nanti
            clearProfileData()
            loadProfileData()
        }

        return view
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)

        val etNameDialog = dialogView.findViewById<EditText>(R.id.et_edit_name)
        val etEducationDialog = dialogView.findViewById<EditText>(R.id.et_edit_education)
        val etSpecDialog = dialogView.findViewById<EditText>(R.id.et_edit_specialization)
        val etScheduleDialog = dialogView.findViewById<EditText>(R.id.et_edit_schedule)

        // Set data awal ke input dialog
        etNameDialog.setText(etFullName.text)
        etEducationDialog.setText(etEducation.text)
        etSpecDialog.setText(etSpecialization.text)
        etScheduleDialog.setText(etSchedule.text)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val btnSave = dialogView.findViewById<Button>(R.id.btn_save_profile)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel_edit)

        btnSave.setOnClickListener {
            val newName = etNameDialog.text.toString()
            val newEdu = etEducationDialog.text.toString()
            val newSpec = etSpecDialog.text.toString()
            val newSched = etScheduleDialog.text.toString()

            // Simpan ke SharedPreferences
            saveProfileData(newName, newEdu, newSpec, newSched)

            // Update UI
            loadProfileData()

            Toast.makeText(requireContext(), "Perubahan disimpan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveProfileData(name: String, edu: String, spec: String, sched: String) {
        with(sharedPrefs.edit()) {
            putString("name", name)
            putString("education", edu)
            putString("specialization", spec)
            putString("schedule", sched)
            apply()
        }
    }

    private fun loadProfileData() {
        etFullName.setText(sharedPrefs.getString("name", "Dr. Jonathan Gregory House"))
        etEducation.setText(sharedPrefs.getString("education", "Universitas Indonesia - Fakultas Kedokteran"))
        etSpecialization.setText(sharedPrefs.getString("specialization", "Penyakit Dalam"))
        etSchedule.setText(sharedPrefs.getString("schedule", "Senin - Jumat : 08.00 - 15.00"))
    }

    private fun clearProfileData() {
        sharedPrefs.edit().clear().apply()
    }
}
