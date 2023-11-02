package dev.darigan.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update


class WorldMap(private val locationClient: LocationClient): ViewModel() {
    private val _location = MutableStateFlow(LatLng(0.0, 0.0))
    val location: StateFlow<LatLng> = _location.asStateFlow()
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        val second = 1000L
        locationClient.getLocationUpdates(second, callback =
        {   newLocation: Location ->
                _location.update { LatLng(
                    newLocation.latitude,newLocation.longitude
                ) }
        }).launchIn(scope)
    }
}

