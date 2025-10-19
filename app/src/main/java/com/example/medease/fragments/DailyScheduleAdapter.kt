package com.example.medease.fragments


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R

class DailyScheduleAdapter(private var schedules: MutableList<ScheduleItem>) :
    RecyclerView.Adapter<DailyScheduleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val patientName: TextView = view.findViewById(R.id.tv_patient_name)
        val appointmentTime: TextView = view.findViewById(R.id.tv_appointment_time)
        val notes: TextView = view.findViewById(R.id.tv_notes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_schedule_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = schedules.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = schedules[position]
        holder.patientName.text = "Nama Pasien: ${item.patientName}"
        holder.appointmentTime.text = "Pukul: ${item.time}"
        holder.notes.text = "Catatan: ${item.notes}"
    }

    fun updateData(newData: List<ScheduleItem>) {
        schedules.clear()
        schedules.addAll(newData)
        notifyDataSetChanged()
    }
}
