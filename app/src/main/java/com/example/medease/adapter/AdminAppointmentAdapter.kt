package com.example.medease.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.data.model.Appointment

class AdminAppointmentAdapter(
    private var data: List<Appointment>,
    private val onAccept: (Appointment) -> Unit,
    private val onReject: (Appointment) -> Unit
) : RecyclerView.Adapter<AdminAppointmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDoctor: TextView = view.findViewById(R.id.tvDoctor)
        val tvInfo: TextView = view.findViewById(R.id.tvInfo)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_appointment, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ap = data[position]

        holder.tvDoctor.text = ap.doctor
        holder.tvInfo.text = "${ap.category} • ${ap.date} • ${ap.time}"

        when (ap.status) {
            "accepted" -> {
                holder.tvStatus.text = "Diterima"
                holder.btnAccept.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
            }
            "rejected" -> {
                holder.tvStatus.text = "Ditolak"
                holder.btnAccept.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
            }
            else -> {
                holder.tvStatus.text = "Menunggu"
                holder.btnAccept.visibility = View.VISIBLE
                holder.btnReject.visibility = View.VISIBLE
            }
        }

        holder.btnAccept.setOnClickListener { onAccept(ap) }
        holder.btnReject.setOnClickListener { onReject(ap) }
    }

    fun updateList(newList: List<Appointment>) {
        data = newList
        notifyDataSetChanged()
    }
}
