package dev.darigan.myapplication

import android.location.Location // Not in vid?
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationUpdates(interval: Long, callback: (Location) -> Unit): Flow<Location>

    class LocationException(message: String): Exception()
}