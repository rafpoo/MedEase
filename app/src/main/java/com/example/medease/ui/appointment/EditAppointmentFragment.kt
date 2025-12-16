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
import com.example.medease.R
import com.example.medease.data.model.Appointment
import com.example.medease.data.model.Doctor
import com.example.medease.database.viewModels.AppointmentViewModel
import com.example.medease.database.viewModels.AppointmentViewModelFactory
import com.example.medease.database.repositories.AppointmentRepository
import com.example.medease.database.repositories.DoctorRepository
import com.example.medease.utils.getDayName
import com.example.medease.utils.showConfirmDialog
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentFragment : Fragment() {

    private val args: EditAppointmentFragmentArgs by navArgs()

    private lateinit var viewModel: AppointmentViewModel

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerDoctor: Spinner
    private lateinit var spinnerTime: Spinner
    private lateinit var etNote: EditText
    private lateinit var tvPickDate: TextView
    private lateinit var btnSave: Button

    private var selectedCategory = ""
    private var selectedTime = ""
    private var selectedDate = ""

    private var appointmentId: String = ""
    private var currentAppointment: Appointment? = null

    private val doctorRepo = DoctorRepository()
    private val appointmentRepo = AppointmentRepository()

    private var allDoctors: List<Doctor> = emptyList()
    private var selectedDoctor: Doctor? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = AppointmentViewModelFactory(appointmentRepo)
        viewModel = ViewModelProvider(this, factory)[AppointmentViewModel::class.java]

        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        spinnerDoctor = view.findViewById(R.id.spinnerDoctor)
        spinnerTime = view.findViewById(R.id.spinnerTime)
        etNote = view.findViewById(R.id.etNote)
        tvPickDate = view.findViewById(R.id.tvPickDate)
        btnSave = view.findViewById(R.id.btnSave)

        appointmentId = args.appointmentId
        viewModel.getById(appointmentId)

        viewModel.appointment.observe(viewLifecycleOwner) { appointment ->
            appointment ?: return@observe
            currentAppointment = appointment
            loadDoctorsAndPrefill(appointment)
        }

        setupCategoryDoctorListeners()
        setupDatePicker()

        btnSave.setOnClickListener {
            showConfirmDialog(requireContext()) {
                saveChanges()
            }
        }
    }

    private fun setupCategoryDoctorListeners() {
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedCategory = parent?.getItemAtPosition(position).toString()
                val doctorsByCategory = allDoctors.filter { it.category == selectedCategory }
                spinnerDoctor.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    doctorsByCategory.map { it.name }
                )
                selectedDoctor = null
                selectedTime = ""
                spinnerTime.adapter = null
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerDoctor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val doctorsByCategory = allDoctors.filter { it.category == selectedCategory }
                selectedDoctor = doctorsByCategory.getOrNull(position)
                selectedDoctor?.let {
                    loadAvailableTimesForEdit(currentAppointment!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
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
                    currentAppointment?.let { loadAvailableTimesForEdit(it) }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun loadAvailableTimesForEdit(appointment: Appointment) {
        val doctor = selectedDoctor ?: return
        val dayName = getDayName(selectedDate)

        appointmentRepo.getBookedTimes(doctor.id, selectedDate) { booked ->
            val availableTimes = doctor.schedules
                .filter {
                    it.day == dayName &&
                            (it.time !in booked || it.time == appointment.time)
                }
                .map { it.time }

            spinnerTime.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                availableTimes
            )

            spinnerTime.setSelection(
                availableTimes.indexOf(appointment.time)
            )
        }
    }


    private fun loadDoctorsAndPrefill(appointment: Appointment) {
        doctorRepo.getAllDoctors { doctors ->
            allDoctors = doctors

            if (doctors.isEmpty()) {
                // Jika doctors collection kosong
                Toast.makeText(requireContext(), "No doctors available. Please try again later.", Toast.LENGTH_LONG).show()

                // Nonaktifkan spinner dan tombol save
                spinnerCategory.isEnabled = false
                spinnerDoctor.isEnabled = false
                spinnerTime.isEnabled = false
                btnSave.isEnabled = false

                // Bersihkan adapter spinner
                spinnerCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf<String>())
                spinnerDoctor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf<String>())
                spinnerTime.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf<String>())
                return@getAllDoctors
            }

            // category spinner
            val categories = doctors.map { it.category }.distinct()
            spinnerCategory.adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, categories)

            val categoryIndex = categories.indexOf(appointment.category)
            spinnerCategory.setSelection(categoryIndex)
            selectedCategory = appointment.category

            // doctors by category
            val doctorsByCategory = doctors.filter { it.category == appointment.category }
            spinnerDoctor.adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                doctorsByCategory.map { it.name })

            selectedDoctor = doctorsByCategory.firstOrNull { it.id == appointment.doctorId }
            spinnerDoctor.setSelection(doctorsByCategory.indexOf(selectedDoctor))

            selectedDate = appointment.date
            tvPickDate.text = selectedDate

            loadAvailableTimesForEdit(appointment)
            etNote.setText(appointment.note)
        }
    }


    private fun saveChanges() {
        if (selectedCategory.isEmpty() || selectedDoctor == null ||
            selectedTime.isEmpty() || selectedDate.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedAppointment = Appointment(
            id = appointmentId,
            userId = FirebaseAuth.getInstance().uid ?: "",
            doctorId = selectedDoctor!!.id,
            doctorName = selectedDoctor!!.name,
            category = selectedCategory,
            date = selectedDate,
            time = selectedTime,
            note = etNote.text.toString(),
            status = currentAppointment?.status ?: "pending"
        )

        viewModel.updateAppointment(updatedAppointment) { success ->
            if (!isAdded) return@updateAppointment
            if (success) {
                Toast.makeText(requireContext(), "Update berhasil", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Update gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
