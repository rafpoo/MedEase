package com.example.medease.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.medease.databinding.FragmentOrderStatusBinding
import com.google.android.material.progressindicator.LinearProgressIndicator

class OrderStatusFragment : Fragment() {

    private var _binding: FragmentOrderStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var tvDeliveryAddress: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvDetails: TextView
    private lateinit var tvNotes: TextView
    private lateinit var progressStatus: LinearProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args = arguments
        val address = args?.getString("delivery_address") ?: "-"
        val details = args?.getString("order_details") ?: "-"
        val totalPrice = args?.getString("total_price") ?: "-"
        val note = args?.getString("order_note") ?: "-"

        binding.tvDeliveryAddress.text = "📍 $address"
        binding.tvDetails.text = "🧾 Detail Pesanan:\n$details"
        binding.tvTotalPrice.text = "💰 Total: $totalPrice"
        binding.tvNotes.text = "📝 Catatan: $note"
        binding.tvStatus.text = "🚚 Status: Pesanan sedang dikirim"
        binding.progressStatus.progress = 60
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_order_status)
//
//        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress)
//        tvStatus = findViewById(R.id.tvStatus)
//        tvTotalPrice = findViewById(R.id.tvTotalPrice)
//        tvDetails = findViewById(R.id.tvDetails)
//        tvNotes = findViewById(R.id.tvNotes)
//        progressStatus = findViewById(R.id.progressStatus)
//
//        // Ambil data dari CheckoutPage
//        val address = intent.getStringExtra("delivery_address") ?: "-"
//        val details = intent.getStringExtra("order_details") ?: "-"
//        val totalPrice = intent.getStringExtra("total_price") ?: "-"
//        val note = intent.getStringExtra("order_note") ?: "-"
//
//        tvDeliveryAddress.text = "📍 $address"
//        tvDetails.text = "🧾 Detail Pesanan:\n$details"
//        tvTotalPrice.text = "💰 Total: $totalPrice"
//        tvNotes.text = "📝 Catatan: $note"
//        tvStatus.text = "🚚 Status: Pesanan sedang dikirim"
//        progressStatus.progress = 60
//    }
}