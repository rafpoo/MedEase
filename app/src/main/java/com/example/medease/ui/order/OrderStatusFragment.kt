package com.example.medease.ui.order

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.medease.databinding.FragmentOrderStatusBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.abs

class OrderStatusFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentOrderStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var driverMarker: Marker
    private lateinit var destinationMarker: Marker

    private val handler = Handler(Looper.getMainLooper())
    private var progress = 60

    private var driverLatLng = LatLng(-6.215, 106.845)
    private val destinationLatLng = LatLng(-6.210, 106.852)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        binding.tvDetails.text = details
        binding.tvTotalPrice.text = "💰 Total: $totalPrice"
        binding.tvNotes.text = "📝 Catatan: $note"
        binding.tvStatus.text = "📦 Kurir sedang menuju lokasi"
        binding.progressStatus.progress = progress

        val mapFragment =
            childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        destinationMarker = googleMap.addMarker(
            MarkerOptions()
                .position(destinationLatLng)
                .title("Tujuan Pengiriman")
        )!!

        driverMarker = googleMap.addMarker(
            MarkerOptions()
                .position(driverLatLng)
                .title("Kurir")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )!!

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(destinationLatLng, 14f)
        )

        startFakeTracking()
    }

    private fun startFakeTracking() {
        handler.postDelayed(object : Runnable {
            override fun run() {

                val latDiff = destinationLatLng.latitude - driverLatLng.latitude
                val lngDiff = destinationLatLng.longitude - driverLatLng.longitude

                if (abs(latDiff) < 0.00005 && abs(lngDiff) < 0.00005) {
                    driverLatLng = destinationLatLng
                    driverMarker.position = driverLatLng
                    binding.progressStatus.progress = 100
                    binding.tvStatus.text = "✅ Kurir telah tiba & bertemu penerima"
                    return
                }

                driverLatLng = LatLng(
                    driverLatLng.latitude + latDiff * 0.1,
                    driverLatLng.longitude + lngDiff * 0.1
                )

                driverMarker.position = driverLatLng

                progress += 4
                if (progress > 95) progress = 95
                binding.progressStatus.progress = progress

                binding.tvStatus.text = when {
                    progress < 75 -> "📦 Kurir sedang menuju lokasi"
                    progress < 95 -> "🚚 Kurir hampir sampai"
                    else -> "📍 Kurir di sekitar lokasi"
                }

                handler.postDelayed(this, 1500)
            }
        }, 1500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
