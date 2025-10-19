package com.example.medease.ui.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.databinding.FragmentOrderMedsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.Serializable

class OrderMedsFragment : Fragment() {
    private var _binding: FragmentOrderMedsBinding? = null
    private val binding get() = _binding!!

    private val allMeds = listOf(
        Med("Paracetamol 500mg", "Obat penurun panas dan pereda nyeri", 15000),
        Med("Amoxicillin 500mg", "Antibiotik untuk infeksi bakteri", 35000),
        Med("Vitamin C 1000mg", "Suplemen daya tahan tubuh", 50000),
        Med("Ibuprofen 200mg", "Pereda nyeri dan antiinflamasi", 25000),
        Med("Obat Batuk Sirup", "Sirup untuk batuk berdahak", 28000),
        Med("Antasida", "Untuk meredakan sakit maag", 10000),
        Med("Cetirizine 10mg", "Antihistamin untuk alergi", 20000)
    )

    private var filteredMeds = allMeds.toMutableList()
    private val cart = mutableMapOf<Med, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderMedsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = MedAdapter(filteredMeds, ::onMedClick)
        binding.rvMeds.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMeds.adapter = adapter

        // 🔍 Fitur pencarian
        binding.searchBar.addTextChangedListener(object : TextWatcher {
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

        // 🛒 Tombol checkout (BottomSheet)
        binding.btnCheckout.setOnClickListener {
            showCartBottomSheet()
        }
    }

    // 🛒 Fungsi untuk menampilkan keranjang di BottomSheet
    private fun showCartBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_cart, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)

        val rvCart = bottomSheetView.findViewById<RecyclerView>(R.id.rvCart)
        val tvEmptyCart = bottomSheetView.findViewById<TextView>(R.id.tvEmptyCart)
        val tvTotalItem = bottomSheetView.findViewById<TextView>(R.id.tvTotalItem)
        val tvTotalPrice = bottomSheetView.findViewById<TextView>(R.id.tvTotalPrice)
        val layoutCartButtons = bottomSheetView.findViewById<LinearLayout>(R.id.layoutCartButtons)
        val btnClose = bottomSheetView.findViewById<Button>(R.id.btnCloseCart)
        val btnCheckoutCart = bottomSheetView.findViewById<Button>(R.id.btnCheckoutCart)

        val totalQty = cart.values.sum()
        val totalPrice = cart.entries.sumOf { it.key.price * it.value }

        if (cart.isEmpty()) {
            rvCart.visibility = View.GONE
            tvEmptyCart.visibility = View.VISIBLE
            layoutCartButtons.visibility = View.GONE
        } else {
            rvCart.visibility = View.VISIBLE
            tvEmptyCart.visibility = View.GONE
            layoutCartButtons.visibility = View.VISIBLE

            val adapterCart = CartAdapter(cart) {
                binding.tvCartCount.text = "${cart.values.sum()} item"
                tvTotalItem.text = "Total: ${cart.values.sum()} item"
                val totalHargaBaru = cart.entries.sumOf { it.key.price * it.value }
                tvTotalPrice.text = "Total Harga: Rp$totalHargaBaru"

                if (cart.isEmpty()) {
                    rvCart.visibility = View.GONE
                    tvEmptyCart.visibility = View.VISIBLE
                    layoutCartButtons.visibility = View.GONE
                }
            }

            rvCart.layoutManager = LinearLayoutManager(requireContext())
            rvCart.adapter = adapterCart
            tvTotalItem.text = "Total: $totalQty item"
            tvTotalPrice.text = "Total Harga: Rp$totalPrice"
        }

        btnClose.setOnClickListener { dialog.dismiss() }

        // 🔹 Navigasi ke fragment OrderStatus
        btnCheckoutCart.setOnClickListener {
            dialog.dismiss()

            val bundle = Bundle()
            bundle.putSerializable("cartData", HashMap(cart))

            findNavController().navigate(R.id.action_orderMeds_to_checkout, bundle)
        }

        dialog.show()
    }

    // 📦 Dialog detail obat
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
        tvPrice.text = "Rp${med.price}"

        var qty = 1
        etQty.setText(qty.toString())

        btnInc.setOnClickListener { etQty.setText((++qty).toString()) }
        btnDec.setOnClickListener { if (qty > 1) etQty.setText((--qty).toString()) }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            cart[med] = (cart[med] ?: 0) + qty
            binding.tvCartCount.text = "${cart.values.sum()} item"
            dialog.dismiss()
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 🧾 Data class dan adapter
    data class Med(val name: String, val desc: String, val price: Int) : Serializable

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
                tvPrice.text = "Rp${med.price}"
                btnDetail.setOnClickListener { onClick(med) }
            }
        }
    }

    class CartAdapter(
        private val cart: MutableMap<Med, Int>,
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
                med: Med,
                qty: Int,
                cart: MutableMap<Med, Int>,
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
                }
            }
        }
    }
}