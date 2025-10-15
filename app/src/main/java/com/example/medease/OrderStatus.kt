package com.example.medease

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator

class OrderStatus : AppCompatActivity() {

    private lateinit var tvDeliveryAddress: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvDetails: TextView
    private lateinit var tvNotes: TextView
    private lateinit var progressStatus: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_status)

        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress)
        tvStatus = findViewById(R.id.tvStatus)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        tvDetails = findViewById(R.id.tvDetails)
        tvNotes = findViewById(R.id.tvNotes)
        progressStatus = findViewById(R.id.progressStatus)

        // Ambil data dari CheckoutPage
        val address = intent.getStringExtra("delivery_address") ?: "-"
        val details = intent.getStringExtra("order_details") ?: "-"
        val totalPrice = intent.getStringExtra("total_price") ?: "-"
        val note = intent.getStringExtra("order_note") ?: "-"

        tvDeliveryAddress.text = "📍 $address"
        tvDetails.text = "🧾 Detail Pesanan:\n$details"
        tvTotalPrice.text = "💰 Total: $totalPrice"
        tvNotes.text = "📝 Catatan: $note"
        tvStatus.text = "🚚 Status: Pesanan sedang dikirim"
        progressStatus.progress = 60
    }
}
