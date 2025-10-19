package com.example.medease.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.data.model.Appointment

class AppointmentAdapter(private var appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDoctor: TextView = itemView.findViewById(R.id.tvDoctorName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.tvDoctor.text = appointment.doctor
        holder.tvCategory.text = appointment.category
        holder.tvDateTime.text = "${appointment.date} • ${appointment.time}"
        holder.tvNote.text = appointment.note
    }

    override fun getItemCount() = appointments.size

    fun updateData(newList: List<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }
}
