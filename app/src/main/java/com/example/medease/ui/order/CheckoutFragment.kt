package com.example.medease.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import java.text.NumberFormat
import java.util.HashMap
import java.util.Locale

class CheckoutFragment : Fragment() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalPrice: TextView
    private lateinit var etAddress: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnPayNow: Button

    private var cart = mutableMapOf<OrderMedsFragment.Med, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checkout, container, false)

        rvCart = view.findViewById(R.id.rvCheckoutCart)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        etAddress = view.findViewById(R.id.etAddress)
        etNotes = view.findViewById(R.id.etNotes)
        btnPayNow = view.findViewById(R.id.btnPayNow)

        // Ambil data cart dari arguments
        @Suppress("UNCHECKED_CAST")
        val receivedCart =
            arguments?.getSerializable("cartData") as? HashMap<OrderMedsFragment.Med, Int>
        if (receivedCart != null) cart = receivedCart.toMutableMap()

        rvCart.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CartAdapter(cart) {
            updateTotalPrice()
        }
        rvCart.adapter = adapter

        updateTotalPrice()

        btnPayNow.setOnClickListener {
            val address = etAddress.text.toString().trim()
            if (address.isEmpty()) {
                Toast.makeText(requireContext(), "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val note = etNotes.text.toString().trim()

            // Hitung total
            val total = cart.entries.sumOf { it.key.price * it.value }

            // Buat string rincian pesanan
            val details = buildString {
                cart.forEach { (med, qty) ->
                    append("- ${med.name} x$qty\n")
                }
            }

            Toast.makeText(
                requireContext(),
                "Pesanan sedang dikirim ke $address 🚚",
                Toast.LENGTH_LONG
            ).show()

            // Kirim data ke halaman OrderStatusFragment via navigation
            val bundle = Bundle().apply {
                putString("delivery_address", address)
                putString("order_details", details)
                putString("total_price", formatRupiah(total))
                putString("order_note", note)
            }

            findNavController().navigate(R.id.action_checkout_to_orderStatus, bundle)
        }

        return view
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
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart, parent, false)
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