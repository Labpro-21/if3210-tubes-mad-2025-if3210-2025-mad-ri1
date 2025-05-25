package com.example.pertamaxify.ui.profile

import android.location.Geocoder
import android.preference.PreferenceManager
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.config.Configuration
import java.util.Locale

//import android.app.Activity
//import android.content.ActivityNotFoundException
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.core.content.ContextCompat
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//import java.net.HttpURLConnection
//import java.net.URL
//
//@Composable
//fun LocationPickerScreen(onDismiss: () -> Unit) {
//    Log.d("LocationPickerScreen", "Bagas")
//    val context = LocalContext.current
//    var countryCode by remember { mutableStateOf<String?>(null) }
//    var selectedLocation by remember { mutableStateOf<String?>(null) }
//
//    // Parse Google Maps URL to extract coordinates
//    fun parseGoogleMapsUrl(
//        url: String,
//        callback: (lat: Double?, lng: Double?, address: String?) -> Unit
//    ) {
//        try {
//            // Example URL: https://www.google.com/maps/place/Eiffel+Tower/@48.8583701,2.2944813,17z
//            val pattern = """@(-?\d+\.\d+),(-?\d+\.\d+)""".toRegex()
//            val matchResult = pattern.find(url)
//
//            if (matchResult != null) {
//                val (latStr, lngStr) = matchResult.destructured
//                val lat = latStr.toDouble()
//                val lng = lngStr.toDouble()
//
//                // Try to extract address name
//                val address = url.substringAfter("place/").substringBefore("/@").replace("+", " ")
//
//                callback(lat, lng, address)
//            } else {
//                callback(null, null, null)
//            }
//        } catch (e: Exception) {
//            callback(null, null, null)
//        }
//    }
//
//    // Parse geo: URI to extract coordinates
//    fun parseGeoUri(uri: String, callback: (lat: Double?, lng: Double?) -> Unit) {
//        try {
//            // Example URI: geo:48.8583701,2.2944813?q=Eiffel+Tower
//            val coordsPart = uri.substringAfter("geo:").substringBefore("?")
//            val parts = coordsPart.split(",")
//
//            if (parts.size >= 2) {
//                val lat = parts[0].toDouble()
//                val lng = parts[1].toDouble()
//                callback(lat, lng)
//            } else {
//                callback(null, null)
//            }
//        } catch (e: Exception) {
//            callback(null, null)
//        }
//    }
//
//    // Helper function to check clipboard for Google Maps link
//    fun checkClipboardForLocationLink(
//        context: Context,
//        callback: (lat: Double?, lng: Double?, address: String?) -> Unit
//    ) {
//        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
//        val item = clipboard?.primaryClip?.getItemAt(0)
//
//        if (item != null) {
//            val text = item.text.toString()
//            when {
//                // Handle Google Maps URL pattern
//                text.contains("maps.google") || text.contains("goo.gl/maps") -> {
//                    parseGoogleMapsUrl(text) { lat, lng, address ->
//                        callback(lat, lng, address)
//                    }
//                }
//                // Handle geo: URL pattern
//                text.startsWith("geo:") -> {
//                    parseGeoUri(text) { lat, lng ->
//                        callback(lat, lng, null)
//                    }
//                }
//                else -> {
//                    callback(null, null, null)
//                }
//            }
//        } else {
//            callback(null, null, null)
//        }
//    }
//
//    // Get country code from coordinates using OpenStreetMap Nominatim
//    fun getCountryCodeFromCoordinates(
//        context: Context,
//        lat: Double,
//        lng: Double,
//        callback: (code: String?) -> Unit
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lng"
//                val connection = URL(url).openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//                connection.setRequestProperty("User-Agent", "YourApp/1.0")
//
//                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
//                    val data = connection.inputStream.bufferedReader().use { it.readText() }
//                    val json = JSONObject(data)
//                    val address = json.optJSONObject("address")
//                    val code = address?.optString("country_code")?.uppercase()
//                    withContext(Dispatchers.Main) {
//                        callback(code)
//                    }
//                } else {
//                    withContext(Dispatchers.Main) {
//                        callback(null)
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                withContext(Dispatchers.Main) {
//                    callback(null)
//                }
//            }
//        }
//    }
//
//    // Location picker launcher
//    val locationPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // Try to get data from intent extras first
//            result.data?.let { data ->
//                val lat = data.getDoubleExtra("latitude", 0.0)
//                val lng = data.getDoubleExtra("longitude", 0.0)
//
//                if (lat != 0.0 || lng != 0.0) {
//                    // If we got coordinates directly
//                    getCountryCodeFromCoordinates(context, lat, lng) { code ->
//                        countryCode = code
//                        selectedLocation = "Lat: $lat, Lng: $lng"
//                    }
//                } else {
//                    // Fallback to clipboard check
//                    checkClipboardForLocationLink(context) { lat, lng, address ->
//                        if (lat != null && lng != null) {
//                            getCountryCodeFromCoordinates(context, lat, lng) { code ->
//                                countryCode = code
//                                selectedLocation = address ?: "Lat: $lat, Lng: $lng"
//                            }
//                        }
//                    }
//                }
//            } ?: run {
//                // No data in intent, check clipboard
//                checkClipboardForLocationLink(context) { lat, lng, address ->
//                    if (lat != null && lng != null) {
//                        getCountryCodeFromCoordinates(context, lat, lng) { code ->
//                            countryCode = code
//                            selectedLocation = address ?: "Lat: $lat, Lng: $lng"
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // Open Google Maps
//    fun openLocationPicker() {
//        try {
//            // Clear previous results
//            selectedLocation = null
//            countryCode = null
//
//            // Create intent for Google Maps
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = Uri.parse("geo:0,0?q=")
//                setPackage("com.google.android.apps.maps")
//                // These extras might help with selection mode
//                putExtra("EXTRA_FROM_NAVIGATION", true)
//                putExtra("EXTRA_SUPPRESS_ACTIVITY_ANIMATION", true)
//            }
//
//            locationPickerLauncher.launch(intent)
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(context, "Google Maps not installed", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // UI
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//
//        Spacer(modifier = Modifier.height(48.dp))
//
//        Button(onClick = { openLocationPicker() }) {
//            Text("Pick Location")
//        }
//
//        Button(onClick = onDismiss) {
//            Text("Cancel")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        selectedLocation?.let { location ->
//            Text("Selected Location: $location", style = MaterialTheme.typography.bodyLarge)
//        }
//
//        countryCode?.let { code ->
//            Text("Country Code: $code", style = MaterialTheme.typography.bodyLarge)
//        }
//    }
//}

@Composable
fun LocationPickerScreen(
    onLocationPicked: (latitude: Double, longitude: Double, countryCode: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }

    AndroidView(
        factory = {
            val mapView = MapView(it)
            Configuration.getInstance().load(it, PreferenceManager.getDefaultSharedPreferences(it))

            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)

            val mapController = mapView.controller
            mapController.setZoom(5.0)
            mapController.setCenter(GeoPoint(0.0, 0.0))

            mapView.overlays.add(object : Overlay() {
                override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
                    e ?: return false
                    mapView ?: return false

                    val projection = mapView.projection
                    val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint

                    selectedLocation = geoPoint

                    val geocoder = Geocoder(context, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
                        val countryCode = addresses?.firstOrNull()?.countryCode ?: "Unknown"

                        onLocationPicked(geoPoint.latitude, geoPoint.longitude, countryCode)
                        onDismiss()
                    } catch (ex: Exception) {
                        Toast.makeText(context, "Failed to get country", Toast.LENGTH_SHORT).show()
                    }

                    return true
                }
            })

            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
}
