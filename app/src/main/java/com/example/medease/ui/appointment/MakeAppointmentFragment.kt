package com.example.medease.ui.appointment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.medease.R
import java.text.SimpleDateFormat
import java.util.*

class MakeAppointmentFragment : Fragment() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerDoctor: Spinner
    private lateinit var spinnerTime: Spinner
    private lateinit var etNote: EditText
    private lateinit var tvPickDate: TextView
    private lateinit var btnConfirm: Button

    private lateinit var imgDoctor: ImageView
    private lateinit var tvDoctorName: TextView
    private lateinit var tvDoctorCategory: TextView
    private lateinit var tvDoctorDesc: TextView
    private lateinit var layoutDoctorInfo: LinearLayout

    private var selectedCategory = ""
    private var selectedDoctor = ""
    private var selectedTime = ""
    private var selectedDate = ""

    // Data kategori, dokter, jam & deskripsi
    private val doctorData = mapOf(
        "General Practitioner" to mapOf(
            "Dr. Sarah Tan" to listOf("09:00 - 09:30", "09:30 - 10:00", "10:00 - 10:30"),
            "Dr. Agus Wirawan" to listOf("13:00 - 13:30", "13:30 - 14:00", "14:00 - 14:30")
        ),
        "Dentist" to mapOf(
            "Dr. Budi Santoso" to listOf("19:00 - 19:30", "19:30 - 20:00", "20:00 - 20:30"),
            "Dr. Rina Kurnia" to listOf("20:30 - 21:00", "21:00 - 21:30")
        ),
        "Cardiologist" to mapOf(
            "Dr. Lisa Kusuma" to listOf("08:00 - 08:30", "08:30 - 09:00", "09:00 - 09:30")
        ),
        "Pediatrician" to mapOf(
            "Dr. Dita Melani" to listOf("10:00 - 10:30", "10:30 - 11:00"),
            "Dr. Andi Wirawan" to listOf("11:00 - 11:30", "11:30 - 12:00")
        )
    )

    private val doctorInfo = mapOf(
        "Dr. Sarah Tan" to DoctorProfile("Dr. Sarah Tan", "General Practitioner",
            "Experienced GP specializing in preventive medicine and teleconsultation.", R.drawable.ic_doctor),
        "Dr. Agus Wirawan" to DoctorProfile("Dr. Agus Wirawan", "General Practitioner",
            "10+ years experience handling general health and chronic illness management.", R.drawable.ic_doctor),
        "Dr. Budi Santoso" to DoctorProfile("Dr. Budi Santoso", "Dentist",
            "Expert in dental surgery and smile reconstruction. Known for gentle touch.", R.drawable.ic_doctor),
        "Dr. Rina Kurnia" to DoctorProfile("Dr. Rina Kurnia", "Dentist",
            "Professional aesthetic dentist specializing in veneers & whitening.", R.drawable.ic_doctor),
        "Dr. Lisa Kusuma" to DoctorProfile("Dr. Lisa Kusuma", "Cardiologist",
            "Heart specialist with focus on non-invasive cardiac care and diagnostics.", R.drawable.ic_doctor),
        "Dr. Dita Melani" to DoctorProfile("Dr. Dita Melani", "Pediatrician",
            "Caring pediatrician passionate about children’s growth and nutrition.", R.drawable.ic_doctor),
        "Dr. Andi Wirawan" to DoctorProfile("Dr. Andi Wirawan", "Pediatrician",
            "Friendly pediatrician focusing on early development and immunization.", R.drawable.ic_doctor)
    )

    data class DoctorProfile(val name: String, val category: String, val description: String, val imageRes: Int)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_make_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View dari layout
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        spinnerDoctor = view.findViewById(R.id.spinnerDoctor)
        spinnerTime = view.findViewById(R.id.spinnerTime)
        etNote = view.findViewById(R.id.etNote)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        tvPickDate = view.findViewById(R.id.tvPickDate)

        imgDoctor = view.findViewById(R.id.imgDoctor)
        tvDoctorName = view.findViewById(R.id.tvDoctorName)
        tvDoctorCategory = view.findViewById(R.id.tvDoctorCategory)
        tvDoctorDesc = view.findViewById(R.id.tvDoctorDesc)
        layoutDoctorInfo = view.findViewById(R.id.layoutDoctorInfo)

        setupSpinners()
        setupDatePicker()
        setupConfirmButton()
    }

    private fun setupSpinners() {
        val categories = doctorData.keys.toList()
        spinnerCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                val doctors = doctorData[selectedCategory]?.keys?.toList() ?: listOf()
                spinnerDoctor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, doctors)
                spinnerTime.adapter = null
                layoutDoctorInfo.visibility = View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerDoctor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDoctor = spinnerDoctor.selectedItem.toString()
                val times = doctorData[selectedCategory]?.get(selectedDoctor) ?: listOf()
                spinnerTime.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, times)

                doctorInfo[selectedDoctor]?.let { profile ->
                    layoutDoctorInfo.visibility = View.VISIBLE
                    imgDoctor.setImageResource(profile.imageRes)
                    tvDoctorName.text = profile.name
                    tvDoctorCategory.text = profile.category
                    tvDoctorDesc.text = profile.description
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTime = spinnerTime.selectedItem.toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDatePicker() {
        tvPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    selectedDate = sdf.format(calendar.time)
                    tvPickDate.text = selectedDate
                    tvPickDate.setTextColor(resources.getColor(R.color.black))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        }
    }

    private fun setupConfirmButton() {
        btnConfirm.setOnClickListener {
            val note = etNote.text.toString()
            if (selectedCategory.isEmpty() || selectedDoctor.isEmpty() || selectedTime.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please complete all selections!", Toast.LENGTH_SHORT).show()
            } else {
                val summary = """
                    ✅ Appointment Created!
                    • Category: $selectedCategory
                    • Doctor: $selectedDoctor
                    • Date: $selectedDate
                    • Time: $selectedTime
                    • Note: $note
                """.trimIndent()
                Toast.makeText(requireContext(), summary, Toast.LENGTH_LONG).show()

                // Gunakan Navigation Component untuk kembali
                findNavController().navigateUp()
            }
        }
    }
}
