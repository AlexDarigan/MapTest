package dev.darigan.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long, callback: (Location) -> Unit): Flow<Location> {
        return callbackFlow {

            // Check permissions are granted
            if(!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing location permission")
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            // Shouldn't this be or?
            if(!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            // Build our location request
            val request = LocationRequest.Builder(interval).build()

            // On each location received, call this
            val locationCallback = object: LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location: Location ->
                        callback(location)
                        //launch { send(location) }
                    }
                }
            }

            // Request locations with our request
            // Define what gets called when called back
            // Set the loop to the main loop
            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper(),
            )

            awaitClose {
                // Stop listening once the flow is closed
                client.removeLocationUpdates(locationCallback)
            }


        }

    }
}