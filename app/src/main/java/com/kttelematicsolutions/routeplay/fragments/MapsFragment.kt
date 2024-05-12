package com.kttelematicsolutions.routeplay.fragments

import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.kttelematicsolutions.routeplay.MainViewModel
import com.kttelematicsolutions.routeplay.R
import com.kttelematicsolutions.routeplay.databinding.FragmentMapsBinding
import com.kttelematicsolutions.routeplay.realmClasses.LocationData

class MapsFragment : Fragment() {
    private lateinit var binding: FragmentMapsBinding
    private lateinit var locationLatLng: LatLng
    private lateinit var callback: OnMapReadyCallback
    private lateinit var id: String
    private lateinit var userId: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var timeStamp: String
    private val viewModel: MainViewModel by viewModels()
    private var previousLocations: List<LocationData> = emptyList()
    private var currentIndex: Int = 0
    private lateinit var _googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getString("id").toString()
        userId = arguments?.getString("userId").toString()
        latitude = arguments?.getDouble("latitude") ?: 0.0
        longitude = arguments?.getDouble("longitude") ?: 0.0
        timeStamp = arguments?.getString("timestamp").toString()
        locationLatLng = LatLng(latitude, longitude)
        callback = OnMapReadyCallback {googleMap ->
            _googleMap = googleMap
            val zoomLevel = 16f
            googleMap.addMarker(MarkerOptions().position(locationLatLng).title("Location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, zoomLevel))
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        binding.title.text = getString(R.string.location_log, userId)
        binding.timeStamp.text = timeStamp
        binding.latitude.text = latitude.toString()
        binding.longitude.text = longitude.toString()
        viewModel.getPreviousLocations(userId, id).observe(viewLifecycleOwner) { locations ->
            previousLocations = locations
        }
        binding.playBackButton.setOnClickListener {
            animateMarker()
        }
        binding.backArrow.setOnClickListener { handleBackPress() }
    }
    private fun animateMarker() {
        if (previousLocations.isNotEmpty()) {
            val handler = Handler(Looper.getMainLooper())
            handler.post(object : Runnable {
                override fun run() {
                    if (currentIndex < previousLocations.size) {
                        val location = previousLocations[currentIndex]
                        val newPosition = LatLng(location.latitude, location.longitude)
                        _googleMap.clear()
                        _googleMap.addMarker(MarkerOptions().position(newPosition).title("Location"))
                        if (currentIndex > 0) {
                            val prevLocation = previousLocations[currentIndex - 1]
                            val prevPosition = LatLng(prevLocation.latitude, prevLocation.longitude)
                            _googleMap.addPolyline(
                                PolylineOptions()
                                    .add(prevPosition, newPosition)
                                    .width(5f)
                                    .color(Color.RED)
                            )
                        }
                        _googleMap.animateCamera(CameraUpdateFactory.newLatLng(newPosition))

                        currentIndex++
                        handler.postDelayed(this, 500)
                    }
                }
            })
        }
    }
    private fun handleBackPress() {
        findNavController().navigateUp()
    }
}