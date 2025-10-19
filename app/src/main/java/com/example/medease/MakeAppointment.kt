package com.example.medease

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MakeAppointment : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerDoctor: Spinner
    private lateinit var spinnerTime: Spinner
    private lateinit var etNote: EditText
    private lateinit var tvPickDate: TextView
    private lateinit var btnConfirm: Button

    // Info dokter di bawah spinner
    private lateinit var imgDoctor: ImageView
    private lateinit var tvDoctorName: TextView
    private lateinit var tvDoctorCategory: TextView
    private lateinit var tvDoctorDesc: TextView
    private lateinit var layoutDoctorInfo: LinearLayout

    private var selectedCategory = ""
    private var selectedDoctor = ""
    private var selectedTime = ""
    private var selectedDate = ""

    // --- Data kategori, dokter, jam & deskripsi ---
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
            "Experienced GP specializing in preventive medicine and teleconsultation.", android.R.drawable.ic_menu_myplaces),
        "Dr. Agus Wirawan" to DoctorProfile("Dr. Agus Wirawan", "General Practitioner",
            "10+ years experience handling general health and chronic illness management.", android.R.drawable.ic_menu_myplaces),
        "Dr. Budi Santoso" to DoctorProfile("Dr. Budi Santoso", "Dentist",
            "Expert in dental surgery and smile reconstruction. Known for gentle touch.", android.R.drawable.ic_menu_myplaces),
        "Dr. Rina Kurnia" to DoctorProfile("Dr. Rina Kurnia", "Dentist",
            "Professional aesthetic dentist specializing in veneers & whitening.", android.R.drawable.ic_menu_myplaces),
        "Dr. Lisa Kusuma" to DoctorProfile("Dr. Lisa Kusuma", "Cardiologist",
            "Heart specialist with focus on non-invasive cardiac care and diagnostics.", android.R.drawable.ic_menu_myplaces),
        "Dr. Dita Melani" to DoctorProfile("Dr. Dita Melani", "Pediatrician",
            "Caring pediatrician passionate about children’s growth and nutrition.", android.R.drawable.ic_menu_myplaces),
        "Dr. Andi Wirawan" to DoctorProfile("Dr. Andi Wirawan", "Pediatrician",
            "Friendly pediatrician focusing on early development and immunization.", android.R.drawable.ic_menu_myplaces)
    )

    data class DoctorProfile(val name: String, val category: String, val description: String, val imageRes: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_appointment)

        // 🔹 Inisialisasi View
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerDoctor = findViewById(R.id.spinnerDoctor)
        spinnerTime = findViewById(R.id.spinnerTime)
        etNote = findViewById(R.id.etNote)
        btnConfirm = findViewById(R.id.btnConfirm)
        tvPickDate = findViewById(R.id.tvPickDate)

        imgDoctor = findViewById(R.id.imgDoctor)
        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvDoctorCategory = findViewById(R.id.tvDoctorCategory)
        tvDoctorDesc = findViewById(R.id.tvDoctorDesc)
        layoutDoctorInfo = findViewById(R.id.layoutDoctorInfo)

        // --- Spinner Kategori ---
        val categories = doctorData.keys.toList()
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                val doctors = doctorData[selectedCategory]?.keys?.toList() ?: listOf()
                spinnerDoctor.adapter = ArrayAdapter(this@MakeAppointment, android.R.layout.simple_spinner_dropdown_item, doctors)
                spinnerTime.adapter = null
                layoutDoctorInfo.visibility = android.view.View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // --- Spinner Dokter ---
        spinnerDoctor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedDoctor = spinnerDoctor.selectedItem.toString()
                val times = doctorData[selectedCategory]?.get(selectedDoctor) ?: listOf()
                spinnerTime.adapter = ArrayAdapter(this@MakeAppointment, android.R.layout.simple_spinner_dropdown_item, times)

                doctorInfo[selectedDoctor]?.let { profile ->
                    layoutDoctorInfo.visibility = android.view.View.VISIBLE
                    imgDoctor.setImageResource(profile.imageRes)
                    tvDoctorName.text = profile.name
                    tvDoctorCategory.text = profile.category
                    tvDoctorDesc.text = profile.description
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // --- Spinner Jam ---
        spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedTime = spinnerTime.selectedItem.toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // --- Pilih Tanggal ---
        tvPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,
                { _, year, month, day ->
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    calendar.set(year, month, day)
                    selectedDate = sdf.format(calendar.time)
                    tvPickDate.text = selectedDate
                    tvPickDate.setTextColor(getColor(android.R.color.black))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        }

        // --- Tombol Konfirmasi ---
        btnConfirm.setOnClickListener {
            val note = etNote.text.toString()
            if (selectedCategory.isEmpty() || selectedDoctor.isEmpty() || selectedTime.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please complete all selections!", Toast.LENGTH_SHORT).show()
            } else {
                val summary = """
                    ✅ Appointment Created!
                    • Category: $selectedCategory
                    • Doctor: $selectedDoctor
                    • Date: $selectedDate
                    • Time: $selectedTime
                    • Note: $note
                """.trimIndent()
                Toast.makeText(this, summary, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
