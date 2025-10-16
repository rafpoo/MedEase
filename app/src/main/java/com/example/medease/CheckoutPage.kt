package com.example.medease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class CheckoutPage : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalPrice: TextView
    private lateinit var etAddress: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnPayNow: Button

    private var cart = mutableMapOf<OrderMedsFragment.Med, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_page)

        rvCart = findViewById(R.id.rvCheckoutCart)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        etAddress = findViewById(R.id.etAddress)
        etNotes = findViewById(R.id.etNotes)
        btnPayNow = findViewById(R.id.btnPayNow)

        @Suppress("UNCHECKED_CAST")
        val receivedCart = intent.getSerializableExtra("cart_data") as? HashMap<OrderMedsFragment.Med, Int>
        if (receivedCart != null) cart = receivedCart.toMutableMap()

        rvCart.layoutManager = LinearLayoutManager(this)
        val adapter = CartAdapter(cart) {
            updateTotalPrice()
        }
        rvCart.adapter = adapter

        updateTotalPrice()

        btnPayNow.setOnClickListener {
            val address = etAddress.text.toString().trim()
            if (address.isEmpty()) {
                Toast.makeText(this, "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val note = etNotes.text.toString().trim()

            // Hitung total dan buat string rincian pesanan
            val total = cart.entries.sumOf { it.key.price * it.value }
            val details = buildString {
                cart.forEach { (med, qty) ->
                    append("- ${med.name} x$qty\n")
                }
            }

            // Langsung tampilkan alert + lanjut ke OrderStatus
            Toast.makeText(this, "Pesanan sedang dikirim ke $address 🚚", Toast.LENGTH_LONG).show()

            val intent = Intent(this, OrderStatusFragment::class.java).apply {
                putExtra("delivery_address", address)
                putExtra("order_details", details)
                putExtra("total_price", formatRupiah(total))
                putExtra("order_note", note)
            }

            // reset cart di OrderMeds
            val resultIntent = Intent()
            resultIntent.putExtra("checkout_message", "Pesanan sedang dikirim ke $address")
            setResult(Activity.RESULT_OK, resultIntent)

            startActivity(intent)
            finish()
        }
    }

    private fun updateTotalPrice() {
        val total = cart.entries.sumOf { it.key.price * it.value }
        tvTotalPrice.text = formatRupiah(total)
        btnPayNow.visibility = if (cart.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun formatRupiah(amount: Int): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(amount).replace(",00", "")
    }

    class CartAdapter(
        private val cart: MutableMap<OrderMedsFragment.Med, Int>,
        private val onQuantityChanged: () -> Unit
    ) : RecyclerView.Adapter<CartAdapter.CartVH>() {

        private val items get() = cart.keys.toList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartVH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
            return CartVH(v)
        }

        override fun onBindViewHolder(holder: CartVH, position: Int) {
            val med = items[position]
            val qty = cart[med] ?: 0
            holder.bind(med, qty, cart, onQuantityChanged)
        }

        override fun getItemCount(): Int = items.size

        class CartVH(v: View) : RecyclerView.ViewHolder(v) {
            private val tvName: TextView = v.findViewById(R.id.tvCartMedName)
            private val tvQty: TextView = v.findViewById(R.id.tvCartQty)
            private val btnInc: Button = v.findViewById(R.id.btnCartIncrease)
            private val btnDec: Button = v.findViewById(R.id.btnCartDecrease)

            fun bind(
                med: OrderMedsFragment.Med,
                qty: Int,
                cart: MutableMap<OrderMedsFragment.Med, Int>,
                onQuantityChanged: () -> Unit
            ) {
                tvName.text = med.name
                tvQty.text = qty.toString()

                btnInc.setOnClickListener {
                    cart[med] = (cart[med] ?: 0) + 1
                    tvQty.text = cart[med].toString()
                    onQuantityChanged()
                }

                btnDec.setOnClickListener {
                    val current = (cart[med] ?: 0) - 1
                    if (current <= 0) cart.remove(med)
                    else cart[med] = current
                    onQuantityChanged()
                    (itemView.parent as? RecyclerView)?.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
}
