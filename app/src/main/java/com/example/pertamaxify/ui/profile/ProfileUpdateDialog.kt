package com.example.pertamaxify.ui.profile

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose
    .material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.pertamaxify.R
import com.example.pertamaxify.data.model.ProfileResponse
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteText
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONObject
import java.util.Locale
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ProfileUpdateDialog(
    onDismiss: () -> Unit,
//    onSave: (String, String, String?, String, String?) -> Unit, // title, artist, imagePath, audioPath, email
    profile: ProfileResponse?,
    mapLocationCode: String?,
    onShowMapClicked: () -> Unit
//    onCountryDetected: (String) -> Unit
) {

    // Main Variables
    val context = LocalContext.current
    var newProfileURI by remember { mutableStateOf<Uri?>(null) }
    var oldLocationCode by remember { mutableStateOf<String?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var tempFileUri by remember { mutableStateOf<Uri?>(null) }
    var shouldLaunchCamera by remember { mutableStateOf(false) }
    var detectedCountry by remember { mutableStateOf<String>("") }
    var locationError by remember { mutableStateOf<String?>(null) }
    var showPermissionRationale by remember { mutableStateOf<Boolean>(false) }
    val detectCountry = remember { mutableStateOf<() -> Unit>({}) }
    var newLocationCode by remember { mutableStateOf<String?>(null) }
//    var selectedLocation by remember { mutableStateOf(profile?.location ?: "") }

//    // State to track if metadata extraction is in progress
//    var isExtracting by remember { mutableStateOf(false) }


    // File creation
    fun createImageFileUri(): Uri? {
        return try {
            val filename = "profile_${System.currentTimeMillis()}.jpg"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: context.filesDir
            val file = File(storageDir, filename)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            Log.e("Camera", "Error creating temp file", e)
            null
        }
    }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { newProfileURI = it }
    }

    // Camera capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            newProfileURI = tempFileUri
        } else {
            Toast.makeText(context, "Photo capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempFileUri = createImageFileUri()
            shouldLaunchCamera = true
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Launch camera if ready
    LaunchedEffect(shouldLaunchCamera, tempFileUri) {
        if (shouldLaunchCamera) {
            val uri = tempFileUri
            if (uri != null) {
                cameraLauncher.launch(uri)
            } else {
                Log.e("Camera", "URI was null when trying to launch camera")
            }
            shouldLaunchCamera = false
        }
    }

    // Image source dialog
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Select Image Source", style = Typography.titleMedium, color = WhiteText) },
            text = {
                Column {
                    Button(
                        onClick = {
                            showImageSourceDialog = false
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                                tempFileUri = createImageFileUri()
                                shouldLaunchCamera = true
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) {
                        Text("Take Photo")
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showImageSourceDialog = false
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        Text("Choose from Gallery")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showImageSourceDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFBB86FC))
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }

//    // Get Country Code
//    fun fetchCountryCode(
//        latitude: Double,
//        longitude: Double,
//        onResult: (String) -> Unit
//    ) {
//        // Using Nominatim API (OpenStreetMap)
//        val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude"
//
//        thread {
//            try {
//                val connection = URL(url).openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//                connection.connect()
//
//                if (connection.responseCode == 200) {
//                    val response = connection.inputStream.bufferedReader().use { it.readText() }
//                    val countryCode = JSONObject(response).getString("country_code")
//                    onResult(countryCode.uppercase())
//                } else {
//                    onResult("")
//                }
//            } catch (e: Exception) {
//                onResult("")
//            }
//        }
//    }
//
//    // Location detection function
//    fun detectCountry() {
//        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//        val cancellationTokenSource = CancellationTokenSource()
//
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            fusedLocationClient.getCurrentLocation(
//                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
//                cancellationTokenSource.token
//            ).addOnSuccessListener { location: Location? ->
//                location?.let {
//                    fetchCountryCode(it.latitude, it.longitude) { countryCode ->
//                        detectedCountry = Locale("", countryCode).displayCountry
//                        // TODO: STORE ISO CODE USING countryCode
//                    }
//                } ?: run {
//                    detectedCountry = "Location not available"
//                }
//            }.addOnFailureListener {
//                detectedCountry = "Location detection failed"
//            }
//        } else {
//            detectedCountry = "Location permission required"
//        }
//        return
//    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            detectCountry.value()
        } else {
            locationError = "Location permission required"
            detectedCountry = ""
        }
    }

    fun getCountryNameFromCode(countryCode: String): String {
        return try {
            Locale("", countryCode).displayCountry
        } catch (e: Exception) {
            "Unknown"
        }
    }

    // Fetch country code from coordinates
    fun fetchCountryCode(latitude: Double, longitude: Double, onResult: (String) -> Unit) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.countryCode?.let { code ->
                onResult(code.uppercase())
            } ?: run {
                locationError = "Could not determine country"
                onResult("")
            }
        } catch (e: Exception) {
            locationError = "Geocoding failed"
            onResult("")
            Log.e("Geocoder", "Error getting country code", e)
        }
    }

    detectCountry.value =  {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()

        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationError = null

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        fetchCountryCode(location.latitude, location.longitude) { countryCode ->
                            val countryName = Locale("", countryCode).displayCountry
                            detectedCountry = countryName
                            oldLocationCode = profile?.location ?: ""
                            newLocationCode = countryCode
                            // TODO: STORE ISO CODE USING countryCode
                        }
                    } else {
                        locationError = "Location not available"
                        detectedCountry = ""
                    }
                }.addOnFailureListener { e ->
                    locationError = "Location detection failed: ${e.localizedMessage}"
                    detectedCountry = ""
                    Log.e("Location", "Failed to get location", e)
                }
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                showPermissionRationale = true  // Trigger composable dialog
            }

            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LaunchedEffect(Unit) {
        detectCountry.value()
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Location Permission Needed") },
            text = { Text("To detect your country, please allow location access") },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationale = false
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                ) {
                    Text("Allow")
                }
            },
            dismissButton = {
                Button(onClick = { showPermissionRationale = false }) {
                    Text("Cancel")
                }
            }
        )
    }

//    // Location picker launcher
//    val locationPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            data?.let {
//                val lat = it.getDoubleExtra("latitude", 0.0)
//                val lng = it.getDoubleExtra("longitude", 0.0)
//                val address = it.getStringExtra("address") ?: "Unknown location"
//
//                selectedLocation = address
//            }
//        }
//    }
//
//    // Open Google Maps
//    fun openLocationPicker() {
//        try {
//            // Create explicit intent for Google Maps
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = Uri.parse("geo:0,0?q=")
//                setPackage("com.google.android.apps.maps")
//                putExtra("EXTRA_EDIT_MODE", true) // Enable selection mode
//            }
//
//            locationPickerLauncher.launch(intent)
//        } catch (e: ActivityNotFoundException) {
//            // Fallback if Google Maps not installed
//            Toast.makeText(context, "Google Maps not installed", Toast.LENGTH_SHORT).show()
//
//            // Alternative: Open general map picker
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                data = Uri.parse("geo:0,0?q=")
//            }
//            locationPickerLauncher.launch(intent)
//        }
//    }

//    if (showMapPicker) {
//        LocationPickerScreen(
//            onLocationPicked = { _, _, countryCode ->
//                newLocationCode = countryCode
//            },
//            onDismiss = {
//                showMapPicker = false
//
//            }
//        )
//    }

    // Main Profile Edit Dialogue
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1C1C1E)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Update Profile", style = Typography.titleLarge, color = WhiteText)

                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier.size(120.dp), contentAlignment = Alignment.BottomEnd
                ) {

                    val photoUrl = profile?.profilePhoto?.let {
                        "http://34.101.226.132:3000/uploads/profile-picture/$it"
                    } ?: "http://34.101.226.132:3000/uploads/profile-picture/dummy.png"

                    Image(
                        painter = rememberAsyncImagePainter(model = newProfileURI ?: photoUrl),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Icon",
                        tint = Color(0xFF007F99),
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(6.dp)
                            .clickable { showImageSourceDialog = true })
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onShowMapClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Select Location", color = Color.White)
                }

                Spacer(Modifier.height(8.dp))

                when {
                    mapLocationCode != null  -> {
                        Text(
                            text = "Detected Location: ${getCountryNameFromCode(mapLocationCode)}",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        );
                        newLocationCode = mapLocationCode
                    }

                    detectedCountry.isNotEmpty() -> {
                        Text(
                            text = "Detected Location: $detectedCountry",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    locationError != null -> {
                        Text(
                            text = locationError!!,
                            color = Color.Red.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    else -> {
                        Text(
                            text = "Location not detected",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
//                            if (title.isNotBlank() && artist.isNotBlank() &&
//                                audioUri != null) {
//
//                                // Store URIs directly as strings
//                                val imageUriString = imageUri.toString()
//                                val audioUriString = audioUri.toString()
//
//                                Log.d("URI:", "Image: $imageUriString, Audio: $audioUriString")
//                                onSave(title, artist, imageUriString, audioUriString, email)
//                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        modifier = Modifier.weight(1f),
                        enabled = newProfileURI != null || oldLocationCode != newLocationCode
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}
