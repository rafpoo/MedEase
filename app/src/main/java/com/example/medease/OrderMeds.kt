package com.example.medease

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderMeds : AppCompatActivity() {

    private lateinit var rvMeds: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var btnCheckout: Button
    private lateinit var tvCartCount: TextView

    private val allMeds = listOf(
        Med("Paracetamol 500mg", "Obat penurun panas dan pereda nyeri", "Rp15.000"),
        Med("Amoxicillin 500mg", "Antibiotik untuk infeksi bakteri", "Rp35.000"),
        Med("Vitamin C 1000mg", "Suplemen daya tahan tubuh", "Rp50.000"),
        Med("Ibuprofen 200mg", "Pereda nyeri dan antiinflamasi", "Rp25.000"),
        Med("Obat Batuk Sirup", "Sirup untuk batuk berdahak", "Rp28.000"),
        Med("Antasida", "Untuk meredakan sakit maag", "Rp10.000"),
        Med("Cetirizine 10mg", "Antihistamin untuk alergi", "Rp20.000")
    )

    private var filteredMeds = allMeds.toMutableList()
    private var totalItems = 0
    private val cart = mutableMapOf<Med, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_meds)

        // ✅ pastikan activity_order_meds.xml punya id berikut:
        // rvMeds, searchBar, btnCheckout, tvCartCount
        rvMeds = findViewById(R.id.rvMeds)
        searchBar = findViewById(R.id.searchBar)
        btnCheckout = findViewById(R.id.btnCheckout)
        tvCartCount = findViewById(R.id.tvCartCount)

        val adapter = MedAdapter(filteredMeds, ::onMedClick)
        rvMeds.layoutManager = LinearLayoutManager(this)
        rvMeds.adapter = adapter

        // fitur pencarian
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                filteredMeds.clear()
                filteredMeds.addAll(
                    if (query.isEmpty()) allMeds
                    else allMeds.filter { it.name.lowercase().contains(query) }
                )
                adapter.notifyDataSetChanged()
            }
        })

        // tombol checkout
        btnCheckout.setOnClickListener {
            val message = if (cart.isEmpty()) {
                "Keranjang kamu kosong 😅"
            } else {
                cart.entries.joinToString("\n") {
                    "- ${it.key.name} x${it.value}"
                } + "\n\nTotal item: $totalItems"
            }
            AlertDialog.Builder(this)
                .setTitle("Keranjang Obat")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun onMedClick(med: Med) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_med_detail, null)
        val tvName = dialogView.findViewById<TextView>(R.id.tvMedName)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tvMedDesc)
        val tvPrice = dialogView.findViewById<TextView>(R.id.tvMedPrice)
        val etQty = dialogView.findViewById<EditText>(R.id.etQuantity)
        val btnInc = dialogView.findViewById<Button>(R.id.btnIncrease)
        val btnDec = dialogView.findViewById<Button>(R.id.btnDecrease)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAddToCart)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)

        tvName.text = med.name
        tvDesc.text = med.desc
        tvPrice.text = med.price

        var qty = 1
        etQty.setText(qty.toString())

        btnInc.setOnClickListener {
            qty++
            etQty.setText(qty.toString())
        }

        btnDec.setOnClickListener {
            if (qty > 1) {
                qty--
                etQty.setText(qty.toString())
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            cart[med] = (cart[med] ?: 0) + qty
            totalItems += qty
            tvCartCount.text = "$totalItems item"
            dialog.dismiss()
        }

        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}

// data & adapter
data class Med(val name: String, val desc: String, val price: String)

class MedAdapter(
    private val items: List<Med>,
    private val onClick: (Med) -> Unit
) : RecyclerView.Adapter<MedAdapter.MedVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_med_card, parent, false)
        return MedVH(v)
    }

    override fun onBindViewHolder(holder: MedVH, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun getItemCount(): Int = items.size

    class MedVH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvName: TextView = v.findViewById(R.id.tvMedName)
        private val tvPrice: TextView = v.findViewById(R.id.tvMedPrice)
        private val btnDetail: Button = v.findViewById(R.id.btnDetail)

        fun bind(med: Med, onClick: (Med) -> Unit) {
            tvName.text = med.name
            tvPrice.text = med.price
            btnDetail.setOnClickListener { onClick(med) }
        }
    }
}
