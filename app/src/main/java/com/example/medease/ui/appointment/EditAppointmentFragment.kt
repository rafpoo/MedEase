package com.example.medease.ui.appointment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.example.medease.R
import com.example.medease.data.model.Appointment
import com.example.medease.database.AppointmentViewModel
import com.example.medease.database.AppointmentViewModelFactory
import com.example.medease.database.TotalDatabase
import com.example.medease.repository.AppointmentRepository
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentFragment : Fragment() {
    private lateinit var viewModel: AppointmentViewModel

    private val args: EditAppointmentFragmentArgs by navArgs()

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerDoctor: Spinner
    private lateinit var spinnerTime: Spinner
    private lateinit var etNote: EditText
    private lateinit var tvPickDate: TextView
    private lateinit var btnSave: Button

    private var selectedCategory = ""
    private var selectedDoctor = ""
    private var selectedTime = ""
    private var selectedDate = ""

    private var appointmentId: Int = args.appointmentId
    private var currentAppointment: Appointment? = null

    // Dummy data (sama seperti di MakeAppointmentFragment)
    private val doctorData = mapOf(
        "General Practitioner" to listOf("Dr. Sarah Tan", "Dr. Agus Wirawan"),
        "Dentist" to listOf("Dr. Budi Santoso", "Dr. Rina Kurnia"),
        "Cardiologist" to listOf("Dr. Lisa Kusuma"),
        "Pediatrician" to listOf("Dr. Dita Melani", "Dr. Andi Wirawan")
    )

    private val timeSlots = listOf(
        "08:00 - 08:30", "08:30 - 09:00", "09:00 - 09:30",
        "09:30 - 10:00", "10:00 - 10:30", "13:00 - 13:30",
        "13:30 - 14:00", "14:00 - 14:30", "19:00 - 19:30"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appContext = requireContext().applicationContext

        val db = TotalDatabase.getInstance(appContext)  // pakai singleton
        val repository = AppointmentRepository(db.appointmentDao())

        val factory = AppointmentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppointmentViewModel::class.java]



        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        spinnerDoctor = view.findViewById(R.id.spinnerDoctor)
        spinnerTime = view.findViewById(R.id.spinnerTime)
        etNote = view.findViewById(R.id.etNote)
        tvPickDate = view.findViewById(R.id.tvPickDate)
        btnSave = view.findViewById(R.id.btnSave)

        appointmentId = args.appointmentId

        viewModel.getById(appointmentId)
        viewModel.selectedAppointment.observe(viewLifecycleOwner) { appointment ->
            if (appointment != null) {
                currentAppointment = appointment
                populateFields(appointment)
            }
        }


        setupSpinners()
        setupDatePicker()

        currentAppointment?.let { appointment ->
            selectedCategory = appointment.category
            selectedDoctor = appointment.doctor
            selectedTime = appointment.time
            selectedDate = appointment.date

            spinnerCategory.setSelection(getCategoryIndex(appointment.category))
            spinnerDoctor.setSelection(getDoctorIndex(appointment.doctor))
            spinnerTime.setSelection(getTimeIndex(appointment.time))
            etNote.setText(appointment.note)
            tvPickDate.text = appointment.date
        }

        btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun setupSpinners() {
        val categories = doctorData.keys.toList()
        spinnerCategory.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedCategory = categories[position]
                val doctors = doctorData[selectedCategory] ?: listOf()
                spinnerDoctor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, doctors)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerDoctor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedDoctor = spinnerDoctor.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTime.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, timeSlots)
        spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedTime = timeSlots[position]
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
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun saveChanges() {
        val note = etNote.text.toString()

        if (selectedCategory.isEmpty() || selectedDoctor.isEmpty() || selectedTime.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedAppointment = Appointment(
            id = appointmentId,
            doctor = selectedDoctor,
            category = selectedCategory,
            date = selectedDate,
            time = selectedTime,
            note = note
        )

        viewModel.update(updatedAppointment)

        Toast.makeText(requireContext(), "Appointment updated!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun getCategoryIndex(category: String): Int {
        val list = doctorData.keys.toList()
        return list.indexOf(category).coerceAtLeast(0)
    }

    private fun getDoctorIndex(doctor: String): Int {
        val doctors = doctorData[selectedCategory] ?: listOf()
        return doctors.indexOf(doctor).coerceAtLeast(0)
    }

    private fun getTimeIndex(time: String): Int {
        return timeSlots.indexOf(time).coerceAtLeast(0)
    }

    private fun populateFields(appointment: Appointment) {
        selectedCategory = appointment.category
        selectedDoctor = appointment.doctor
        selectedTime = appointment.time
        selectedDate = appointment.date

        spinnerCategory.setSelection(getCategoryIndex(appointment.category))
        spinnerDoctor.setSelection(getDoctorIndex(appointment.doctor))
        spinnerTime.setSelection(getTimeIndex(appointment.time))

        etNote.setText(appointment.note)
        tvPickDate.text = appointment.date
    }

}
