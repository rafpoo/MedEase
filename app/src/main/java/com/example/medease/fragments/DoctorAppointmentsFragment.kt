package com.example.medease.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.medease.R

class DoctorAppointmentsFragment : Fragment() {

    private var appointmentStatus: String? = null // null = belum ada keputusan

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_appointments, container, false)

        val btnReject = view.findViewById<Button>(R.id.btn_reject)
        val btnAccept = view.findViewById<Button>(R.id.btn_accept)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)

        fun updateStatusUI() {
            when (appointmentStatus) {
                "accepted" -> {
                    btnReject.visibility = View.GONE
                    btnAccept.visibility = View.GONE
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "✅ Diterima"
                }
                "rejected" -> {
                    btnReject.visibility = View.GONE
                    btnAccept.visibility = View.GONE
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "❌ Ditolak"
                }
                else -> {
                    btnReject.visibility = View.VISIBLE
                    btnAccept.visibility = View.VISIBLE
                    tvStatus.visibility = View.GONE
                }
            }
        }

        // Klik tombol Terima
        btnAccept.setOnClickListener {
            appointmentStatus = "accepted"
            Toast.makeText(requireContext(), "Reservasi diterima ✅", Toast.LENGTH_SHORT).show()
            updateStatusUI()
        }

        // Klik tombol Tolak
        btnReject.setOnClickListener {
            showRejectReasonDialog {
                appointmentStatus = "rejected"
                updateStatusUI()
            }
        }

        updateStatusUI()
        return view
    }

    private fun showRejectReasonDialog(onRejected: () -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reject_reason, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group_reasons)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Alasan Penolakan")
            .setView(dialogView)
            .setPositiveButton("Kirim", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val selectedId = radioGroup.checkedRadioButtonId
                if (selectedId == -1) {
                    Toast.makeText(requireContext(), "Pilih alasan terlebih dahulu", Toast.LENGTH_SHORT).show()
                } else {
                    val reason =
                        dialogView.findViewById<RadioButton>(selectedId).text.toString()
                    Toast.makeText(requireContext(), "Reservasi ditolak: $reason", Toast.LENGTH_LONG)
                        .show()
                    dialog.dismiss()
                    onRejected()
                }
            }
        }

        dialog.show()
    }
}
