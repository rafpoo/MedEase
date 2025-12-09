package com.example.medease.adapter

import android.graphics.Color
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
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
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

        when (appointment.status) {

            "pending" -> {
                holder.tvStatus.text = "Pending"
                holder.tvStatus.setTextColor(Color.parseColor("#FFC107"))
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.circle_pending, 0, 0, 0
                )
            }

            "accepted" -> {
                holder.tvStatus.text = "Accepted"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.circle_accepted, 0, 0, 0
                )
            }

            "declined" -> {
                holder.tvStatus.text = "Declined"
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"))
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.circle_declined, 0, 0, 0
                )
            }

            else -> {
                holder.tvStatus.text = "Unknown"
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    override fun getItemCount() = appointments.size

    fun updateData(newList: List<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }
}
