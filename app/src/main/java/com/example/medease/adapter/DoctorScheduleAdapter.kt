package com.example.medease.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.data.model.Appointment
import com.example.medease.R

class DoctorScheduleAdapter(
    private val list: MutableList<Appointment>
) : RecyclerView.Adapter<DoctorScheduleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tv_schedule_date)
        val tvTime: TextView = view.findViewById(R.id.tv_schedule_time)
        val tvPatient: TextView = view.findViewById(R.id.tv_schedule_patient)
        val tvDoctor: TextView = view.findViewById(R.id.tv_schedule_doctor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvDate.text = item.date
        holder.tvTime.text = item.time
        holder.tvPatient.text = "Pasien: ${item.userName}"
        holder.tvDoctor.text = "Dokter: ${item.doctorName}"
    }

    fun setData(newList: List<Appointment>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
