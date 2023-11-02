package dev.darigan.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
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
    var title by remember { mutableIntStateOf(0) }



    val dublin = LatLng(53.3498, -6.2603)
    val london = LatLng(51.5072, 0.1276)
    val singapore = LatLng(1.3521, 103.8198)
    val locations = listOf(
        dublin, london, singapore
    )

    Column(modifier = Modifier
        .padding(0.dp)
        .fillMaxSize()) {

        gradientSky(modifier = Modifier.fillMaxWidth())
        WorldMapComposable(title, location, modifier.fillMaxHeight(.9f))
        Row(modifier = Modifier.weight(.2f)) {
            Text(text = "Location: (${location.latitude}, ${location.longitude})",
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                color = MaterialTheme.colorScheme.primary)
            Button(onClick = {
                title = (title + 1) % 3
            }) {
                Text("Click Me (Title is $title)")
            }
        }

    }
}

// Needs parrallex markers
@Composable
fun gradientSky(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.sky),
        contentDescription = "Background" ,
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(255, 255, 255),
                        Color(255, 255, 255, 0)
                    )
                )
            )
            //.fillMaxSize()
    )
}

@Composable
fun WorldMapComposable(title: Int, location: LatLng, modifier: Modifier = Modifier) {

    val options = GoogleMapOptions().liteMode(true)
    GoogleMap(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(255, 255, 255),
                        Color(255, 255, 255, 0)
                    )
                )
            ),
        cameraPositionState = CameraPositionState(position = CameraPosition(location, 18f, 67.5f, 114f)),
        properties = MapProperties(
            mapType = MapType.NORMAL,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
            LocalContext.current, R.raw.style_json)
        )) {
        Marker(
            state = MarkerState(position = location),
            title = "Your Location $title",
            snippet = "My Location"
        )
    }
}