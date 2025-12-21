package com.example.medease

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DeliveryStatusActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_status)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        val lokasiTujuan = LatLng(-6.200000, 106.816666) // Jakarta

        googleMap.addMarker(
            MarkerOptions()
                .position(lokasiTujuan)
                .title("Lokasi Pengiriman")
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiTujuan, 15f))
    }
}
