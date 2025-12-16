package com.example.medease.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.data.model.Doctor
import com.example.medease.databinding.ItemDoctorAdminBinding
import com.google.android.material.chip.Chip

class AdminDoctorAdapter(
    private val onEdit: (Doctor) -> Unit,
    private val onDelete: (Doctor) -> Unit
) : RecyclerView.Adapter<AdminDoctorAdapter.ViewHolder>() {

    private val data = mutableListOf<Doctor>()

    fun submitList(list: List<Doctor>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    // ✅ WAJIB
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDoctorAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    // ✅ WAJIB
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(
        private val binding: ItemDoctorAdminBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(doctor: Doctor) {
            binding.tvDoctorName.text = doctor.name
            binding.tvDoctorCategory.text = doctor.category

            // 🔥 RESET CHIP GROUP
            binding.chipGroupSchedules.removeAllViews()

            doctor.schedules.forEach { schedule ->
                val chip = Chip(binding.root.context).apply {
                    text = "${schedule.day} • ${schedule.time}"
                    isClickable = false
                    isCheckable = false
                }
                binding.chipGroupSchedules.addView(chip)
            }

            binding.btnEdit.setOnClickListener { onEdit(doctor) }
            binding.btnDelete.setOnClickListener { onDelete(doctor) }
        }
    }
}
