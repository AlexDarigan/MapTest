package dev.darigan.myapplication

import android.location.Location // Not in vid?
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()
}