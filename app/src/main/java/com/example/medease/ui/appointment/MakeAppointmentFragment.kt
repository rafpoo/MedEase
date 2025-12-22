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
import com.example.medease.R
import com.example.medease.data.model.Appointment
import com.example.medease.data.model.Doctor
import com.example.medease.database.repositories.AppointmentRepository
import com.example.medease.database.repositories.DoctorRepository
import com.example.medease.database.viewModels.AppointmentViewModel
import com.example.medease.database.viewModels.AppointmentViewModelFactory
import com.example.medease.utils.getDayName
import com.example.medease.utils.showConfirmDialog
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MakeAppointmentFragment : Fragment() {
    private lateinit var viewModel: AppointmentViewModel

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
//    private var selectedDoctor = ""
    private var selectedTime = ""
    private var selectedDate = ""

    private val doctorRepo = DoctorRepository()
    private val repository = AppointmentRepository()
    private val factory = AppointmentViewModelFactory(repository)

    private var selectedDoctor: Doctor? = null
    private var allDoctors: List<Doctor> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_make_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = AppointmentRepository()
        val factory = AppointmentViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[AppointmentViewModel::class.java]

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
        loadCategories()
        setupDatePicker()
        setupConfirmButton()
    }

    private fun setupSpinners() {
        val categories = allDoctors.map { it.category }.distinct()
        spinnerCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                selectedCategory = parent?.getItemAtPosition(position).toString()

                doctorRepo.getDoctorsByCategory(selectedCategory) { doctors ->
                    allDoctors = doctors
                    spinnerDoctor.adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        doctors.map { it.name }
                    )
                }

                selectedDoctor = null
                selectedTime = ""
                spinnerTime.adapter = null
                layoutDoctorInfo.visibility = View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerDoctor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (allDoctors.isEmpty()) return
                selectedDoctor = allDoctors[position]


                if (selectedDate.isNotEmpty()) {
                    loadAvailableTimes()
                }

                tvDoctorName.text = selectedDoctor!!.name
                tvDoctorCategory.text = selectedDoctor!!.category
                tvDoctorDesc.text = selectedDoctor!!.description
                layoutDoctorInfo.visibility = View.VISIBLE

                spinnerTime.adapter = null
                selectedTime = ""
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

    private fun loadAvailableTimes() {
        val doctor = selectedDoctor ?: return
        val dayName = getDayName(selectedDate)

        repository.getBookedTimes(doctor.id, selectedDate) { bookedTimes ->

            val availableTimes = doctor.schedules
                .filter { it.day == dayName }
                .map { it.time }
                .filter { it !in bookedTimes }

            spinnerTime.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                availableTimes
            )

            if (availableTimes.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Jadwal dokter sudah penuh pada hari ini!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }




    private fun setupDatePicker() {
        tvPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    selectedDate = sdf.format(calendar.time)
                    tvPickDate.text = selectedDate

                    if (selectedDoctor != null) {
                        loadAvailableTimes()
                    }
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

    private fun loadCategories() {
        doctorRepo.getAllDoctors { doctors ->
            allDoctors = doctors

            if (doctors.isEmpty()) {
                // Jika doctors collection kosong
                Toast.makeText(requireContext(), "Tidak ada dokter tersedia. Coba lagi nanti.", Toast.LENGTH_LONG).show()

                // Nonaktifkan spinner dan tombol confirm
                spinnerCategory.isEnabled = false
                spinnerDoctor.isEnabled = false
                spinnerTime.isEnabled = false
                btnConfirm.isEnabled = false

                // Bersihkan adapter spinner
                spinnerCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf<String>())
                spinnerDoctor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf<String>())
                spinnerTime.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf<String>())
                return@getAllDoctors
            }

            val categories = doctors.map { it.category }.distinct()
            spinnerCategory.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )
        }
    }




    private fun setupConfirmButton() {
        btnConfirm.setOnClickListener {

            showConfirmDialog(requireContext()) {

                val note = etNote.text.toString()

                if (selectedCategory.isEmpty() ||
                    selectedDoctor == null ||
                    selectedTime.isEmpty() ||
                    selectedDate.isEmpty()
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Tolong isi semua pilihan!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@showConfirmDialog
                }

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId == null) {
                    Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show()
                    return@showConfirmDialog
                }

                val newAppointment = Appointment(
                    id = "",
                    userId = userId,
                    doctorId = selectedDoctor!!.id,
                    doctorName = selectedDoctor!!.name,
                    category = selectedCategory,
                    date = selectedDate,
                    time = selectedTime,
                    note = note
                )

                viewModel.createAppointment(newAppointment) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Appointment berhasil dibuat!", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(requireContext(), "Gagal membuat appointment", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }



}
