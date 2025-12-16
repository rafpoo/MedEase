package com.example.medease.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.data.model.Appointment
import com.example.medease.utils.showConfirmDialog

class ManageAppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onEdit: (Appointment) -> Unit,
    private val onDelete: (Appointment) -> Unit
) : RecyclerView.Adapter<ManageAppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDoctor: TextView = view.findViewById(R.id.tvDoctor)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
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

        // 🔹 Doctor info
        holder.tvDoctor.text =
            "${appointment.doctorName} • ${appointment.category}"

        // 🔹 Date & time
        holder.tvDateTime.text =
            "${appointment.date} • ${appointment.time}"

        // 🔹 Note
        holder.tvNote.text =
            appointment.note.ifBlank { "-" }

        // 🔹 Delete (selalu boleh)
        holder.btnDelete.setOnClickListener {
            showConfirmDialog(
                holder.itemView.context,
                "Hapus appointment ini?"
            ) {
                onDelete(appointment)
            }
        }

        // 🔹 Edit hanya jika pending
        val editable = appointment.status == "pending"
        holder.btnEdit.isEnabled = editable
        holder.btnEdit.alpha = if (editable) 1f else 0.4f

        holder.btnEdit.setOnClickListener {
            if (!editable) return@setOnClickListener

            showConfirmDialog(
                holder.itemView.context,
                "Edit appointment ini?"
            ) {
                onEdit(appointment)
            }
        }

        // 🔹 Status UI
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
                holder.tvStatus.setTextColor(Color.GRAY)
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    fun updateData(newList: List<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }
}

