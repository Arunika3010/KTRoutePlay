package com.kttelematicsolutions.routeplay

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userId: String
    private lateinit var viewModel: MainViewModel

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locations = locationResult.lastLocation
            val currentTime = System.currentTimeMillis()
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val timeStamp = sdf.format(Date(currentTime))
            if (locations != null) {
                // Save the location data to Realm
                // You need to implement the saveLocationDataToRealm() method
                saveLocationDataToRealm(userId, locations, timeStamp)
                Log.d("TAG", "$userId : $locations")
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("userId") ?: ""
        viewModel = MainViewModel()
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onBind(intent: Intent): IBinder? {
        // Since we don't need any client interaction, we can return null.
        return null
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 15 * 60 * 1000L // 15 minutes
            fastestInterval = 15 * 60 * 1000L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // You need to implement this method to save the location data to Realm
    private fun saveLocationDataToRealm(userId: String, location: Location, timeStamp: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val placeName = address.getAddressLine(0)
                val cityName = address.locality
            }
        }
        viewModel.saveLocationDataToRealm(userId, location, timeStamp)
    }
}