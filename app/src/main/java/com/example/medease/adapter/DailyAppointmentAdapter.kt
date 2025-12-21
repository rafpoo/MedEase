package com.example.medease.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.data.model.Appointment

class DailyAppointmentAdapter(
    private var appointments: MutableList<Appointment>
) : RecyclerView.Adapter<DailyAppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPatientName: TextView = view.findViewById(R.id.tvPatientName)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvNotes: TextView = view.findViewById(R.id.tvNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_schedule_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = appointments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]

        // Gunakan ID sesuai layout asli
        holder.tvPatientName.text = "Spesialis: ${appointment.category ?: "Tidak diketahui"}"
        holder.tvDateTime.text = "Pukul: ${appointment.time ?: "-"}"
        holder.tvNotes.text = "Catatan: ${appointment.note ?: "-"}"
    }

    fun updateData(newData: List<Appointment>) {
        appointments.clear()
        appointments.addAll(newData)
        notifyDataSetChanged()
    }
}
