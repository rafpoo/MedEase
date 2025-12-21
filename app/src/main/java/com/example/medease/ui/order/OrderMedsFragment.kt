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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R
import com.example.medease.databinding.FragmentOrderMedsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class OrderMedsFragment : Fragment() {
    private var _binding: FragmentOrderMedsBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var allMeds = mutableListOf<Med>()
    private var filteredMeds = mutableListOf<Med>()
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

        // 🔥 Load medicines dan cart dari Firestore
        loadMedicinesFromFirestore(adapter)
        loadCartFromFirestore()

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

    // 🔥 Load medicines dari Firestore
    private fun loadMedicinesFromFirestore(adapter: MedAdapter) {
        firestore.collection("medicines")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Jika belum ada data, init data default
                    initDefaultMedicines()
                } else {
                    allMeds.clear()
                    for (doc in documents) {
                        val med = Med(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            desc = doc.getString("desc") ?: "",
                            price = (doc.getLong("price") ?: 0).toInt(),
                            stock = (doc.getLong("stock") ?: 0).toInt()
                        )
                        allMeds.add(med)
                    }
                    filteredMeds.clear()
                    filteredMeds.addAll(allMeds)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal memuat obat: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔥 Init data obat default (hanya sekali)
    private fun initDefaultMedicines() {
        val defaultMeds = listOf(
            hashMapOf("name" to "Paracetamol 500mg", "desc" to "Obat penurun panas dan pereda nyeri", "price" to 15000, "stock" to 100),
            hashMapOf("name" to "Amoxicillin 500mg", "desc" to "Antibiotik untuk infeksi bakteri", "price" to 35000, "stock" to 50),
            hashMapOf("name" to "Vitamin C 1000mg", "desc" to "Suplemen daya tahan tubuh", "price" to 50000, "stock" to 200),
            hashMapOf("name" to "Ibuprofen 200mg", "desc" to "Pereda nyeri dan antiinflamasi", "price" to 25000, "stock" to 80),
            hashMapOf("name" to "Obat Batuk Sirup", "desc" to "Sirup untuk batuk berdahak", "price" to 28000, "stock" to 60),
            hashMapOf("name" to "Antasida", "desc" to "Untuk meredakan sakit maag", "price" to 10000, "stock" to 150),
            hashMapOf("name" to "Cetirizine 10mg", "desc" to "Antihistamin untuk alergi", "price" to 20000, "stock" to 120)
        )

        defaultMeds.forEach { med ->
            firestore.collection("medicines").add(med)
        }

        Toast.makeText(requireContext(), "Data obat berhasil diinisialisasi", Toast.LENGTH_SHORT).show()
    }

    // 🔥 Load cart dari Firestore
    private fun loadCartFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("carts")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    cart.clear()
                    val items = document.get("items") as? List<HashMap<String, Any>> ?: emptyList()

                    items.forEach { item ->
                        val id = item["id"] as? String ?: ""
                        val name = item["name"] as? String ?: ""
                        val desc = item["desc"] as? String ?: ""
                        val price = (item["price"] as? Long)?.toInt() ?: 0
                        val stock = (item["stock"] as? Long)?.toInt() ?: 0
                        val qty = (item["quantity"] as? Long)?.toInt() ?: 0

                        val med = Med(id, name, desc, price, stock)
                        cart[med] = qty
                    }

                    updateCartCount()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal memuat keranjang: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔥 Simpan cart ke Firestore
    private fun saveCartToFirestore() {
        val userId = auth.currentUser?.uid ?: return

        val cartItems = cart.map { (med, qty) ->
            hashMapOf(
                "id" to med.id,
                "name" to med.name,
                "desc" to med.desc,
                "price" to med.price,
                "stock" to med.stock,
                "quantity" to qty
            )
        }

        val cartData = hashMapOf(
            "userId" to userId,
            "items" to cartItems,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("carts")
            .document(userId)
            .set(cartData)
            .addOnSuccessListener {
                // Berhasil disimpan
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal menyimpan keranjang: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔥 Hapus cart dari Firestore
    private fun clearCartFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("carts")
            .document(userId)
            .delete()
    }

    // 🔥 Update stok obat di Firestore setelah checkout
    private fun updateMedicineStock() {
        cart.forEach { (med, qty) ->
            val newStock = med.stock - qty
            if (newStock >= 0) {
                firestore.collection("medicines")
                    .document(med.id)
                    .update("stock", newStock)
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Gagal update stok: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Update cart count
    private fun updateCartCount() {
        binding.tvCartCount.text = "${cart.values.sum()} item"
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
                updateCartCount()
                tvTotalItem.text = "Total: ${cart.values.sum()} item"
                val totalHargaBaru = cart.entries.sumOf { it.key.price * it.value }
                tvTotalPrice.text = "Total Harga: Rp$totalHargaBaru"

                // 🔥 Simpan perubahan ke Firestore
                saveCartToFirestore()

                if (cart.isEmpty()) {
                    rvCart.visibility = View.GONE
                    tvEmptyCart.visibility = View.VISIBLE
                    layoutCartButtons.visibility = View.GONE
                    clearCartFromFirestore()
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

            // 🔥 Update stok obat
            updateMedicineStock()

            // 🔥 Hapus cart dari Firestore setelah checkout
            clearCartFromFirestore()
            cart.clear()
            updateCartCount()

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
        val tvStock = dialogView.findViewById<TextView>(R.id.tvMedStock)
        val etQty = dialogView.findViewById<EditText>(R.id.etQuantity)
        val btnInc = dialogView.findViewById<Button>(R.id.btnIncrease)
        val btnDec = dialogView.findViewById<Button>(R.id.btnDecrease)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAddToCart)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)

        tvName.text = med.name
        tvDesc.text = med.desc
        tvPrice.text = "Rp${med.price}"
        tvStock.text = "Stok: ${med.stock}"

        var qty = 1
        etQty.setText(qty.toString())

        btnInc.setOnClickListener {
            if (qty < med.stock) {
                etQty.setText((++qty).toString())
            } else {
                Toast.makeText(requireContext(), "Stok tidak cukup", Toast.LENGTH_SHORT).show()
            }
        }
        btnDec.setOnClickListener { if (qty > 1) etQty.setText((--qty).toString()) }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            val currentCartQty = cart[med] ?: 0
            if (currentCartQty + qty > med.stock) {
                Toast.makeText(requireContext(), "Stok tidak mencukupi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cart[med] = currentCartQty + qty
            updateCartCount()

            // 🔥 Simpan ke Firestore
            saveCartToFirestore()

            Toast.makeText(requireContext(), "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
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
    data class Med(
        val id: String = "",
        val name: String,
        val desc: String,
        val price: Int,
        val stock: Int = 0
    ) : Serializable

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
            private val tvStock: TextView = v.findViewById(R.id.tvMedStock)
            private val btnDetail: Button = v.findViewById(R.id.btnDetail)

            fun bind(med: Med, onClick: (Med) -> Unit) {
                tvName.text = med.name
                tvPrice.text = "Rp${med.price}"
                tvStock.text = "Stok: ${med.stock}"
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
                    val currentQty = cart[med] ?: 0
                    if (currentQty < med.stock) {
                        cart[med] = currentQty + 1
                        tvQty.text = cart[med].toString()
                        onQuantityChanged()
                    }
                }

                btnDec.setOnClickListener {
                    val current = (cart[med] ?: 0) - 1
                    if (current <= 0) {
                        cart.remove(med)
                    } else {
                        cart[med] = current
                        tvQty.text = current.toString()
                    }
                    onQuantityChanged()
                }
            }
        }
    }
}