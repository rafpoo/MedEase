package com.example.medease.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.data.model.Appointment
import com.example.medease.database.repositories.UserRepository
import com.example.medease.databinding.ItemAppointmentAdminBinding

class AdminAppointmentAdapter(
    private val onAccept: (Appointment) -> Unit,
    private val onDecline: (Appointment) -> Unit
) : ListAdapter<Appointment, AdminAppointmentAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemAppointmentAdminBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Appointment) {
            binding.tvDoctorName.text = data.doctorName
            binding.tvUserName.text = data.userName
            binding.tvDate.text = data.date
            binding.tvTime.text = data.time
            binding.tvDescription.text = data.note

            // === STATUS VISUAL ===
            when (data.status) {
                "accepted" -> {
                    binding.tvStatus.visibility = View.VISIBLE
                    binding.tvStatus.text = "Diterima"
                    binding.tvStatus.setBackgroundColor(Color.parseColor("#1DBF73"))
                }
                "declined" -> {
                    binding.tvStatus.visibility = View.VISIBLE
                    binding.tvStatus.text = "Ditolak"
                    binding.tvStatus.setBackgroundColor(Color.parseColor("#D9534F"))
                }
                else -> {
                    binding.tvStatus.visibility = View.GONE
                }
            }


            // === ONCLICK BUTTON ===
            binding.btnAccept.setOnClickListener { onAccept(data) }
            binding.btnDecline.setOnClickListener { onDecline(data) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppointmentAdminBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(old: Appointment, new: Appointment) = old.id == new.id
        override fun areContentsTheSame(old: Appointment, new: Appointment) = old == new
    }
}

