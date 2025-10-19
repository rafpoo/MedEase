package com.example.medease.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.data.model.Appointment

class ManageAppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onEdit: (Appointment) -> Unit,
    private val onDelete: (Appointment) -> Unit
) : RecyclerView.Adapter<ManageAppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDoctor: TextView = view.findViewById(R.id.tvDoctor)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = appointments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.tvDoctor.text = appointment.doctor
        holder.tvDateTime.text = "${appointment.date} • ${appointment.time}"

        holder.btnEdit.setOnClickListener { onEdit(appointment) }
        holder.btnDelete.setOnClickListener { onDelete(appointment) }
    }

    fun updateData(newList: List<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }
}
