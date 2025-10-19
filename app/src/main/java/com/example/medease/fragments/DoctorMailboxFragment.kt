package com.example.medease.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R

data class Mail(
    val title: String,
    val content: String
)

class DoctorMailboxFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MailAdapter
    private val mailList = mutableListOf<Mail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_mailbox, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMailbox)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data surat (bisa diganti dari backend nanti)
        mailList.add(
            Mail(
                "Permintaan Jadwal Konsultasi",
                "Halo Dokter, saya ingin berkonsultasi mengenai hasil tes darah saya minggu lalu. Apakah dokter tersedia hari Kamis?"
            )
        )
        mailList.add(
            Mail(
                "Ucapan Terima Kasih",
                "Terima kasih atas konsultasi kemarin Dokter. Penjelasan Anda sangat membantu saya memahami kondisi saya."
            )
        )
        mailList.add(
            Mail(
                "Pertanyaan Obat",
                "Dok, apakah saya boleh melanjutkan obat yang diberikan dua minggu lalu jika masih ada sisa?"
            )
        )

        adapter = MailAdapter(mailList) { mail ->
            showMailDetail(mail)
        }

        recyclerView.adapter = adapter
        return view
    }

    private fun showMailDetail(mail: Mail) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_mail_detail, null)
        val title = dialogView.findViewById<TextView>(R.id.tv_mail_detail_title)
        val content = dialogView.findViewById<TextView>(R.id.tv_mail_detail_content)

        title.text = mail.title
        content.text = mail.content

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
            .show()
    }
}
