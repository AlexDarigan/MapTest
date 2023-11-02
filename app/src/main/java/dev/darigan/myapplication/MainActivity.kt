package dev.darigan.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import dev.darigan.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )

        val tracker = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

        val worldMap = WorldMap(tracker)

        setContent {
            MyApplicationTheme {
                Game(worldMap)
            }
        }
    }
}

@Composable
fun Game(worldMap: WorldMap, modifier: Modifier = Modifier) {
    val location by worldMap.location.collectAsState()

    Column {
        Text(text = "Location: (${location.latitude}, ${location.longitude})")
        WorldMapComposable(location)
    }
}

@Composable
fun WorldMapComposable(location: LatLng, modifier: Modifier = Modifier) {

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = CameraPositionState(position = fromLatLngZoom(location, 100f)),
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        Marker(
            state = MarkerState(position = location),
            title = "Your Location",
            snippet = "My Location"
        )
    }
}