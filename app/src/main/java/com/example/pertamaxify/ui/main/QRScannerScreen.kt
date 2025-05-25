package com.example.pertamaxify.ui.main

import android.Manifest
import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.pertamaxify.ui.theme.RedBackground
import com.example.pertamaxify.ui.theme.WhiteText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(
    onDeepLinkFound: (Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var status by remember { mutableStateOf("Scan a QR code") }
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(true) {
        cameraPermission.launchPermissionRequest()
    }

    Surface(color = RedBackground, modifier = Modifier.fillMaxSize()) {
        if (cameraPermission.status.isGranted) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = androidx.camera.core.Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                            val selector = CameraSelector.DEFAULT_BACK_CAMERA
                            val scanner = BarcodeScanning.getClient()

                            val analysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                processImageProxy(imageProxy, scanner) { content ->
                                    if (content.startsWith("purrytify://song/")) {
                                        val id = content.substringAfterLast("/").toIntOrNull()

                                        if (id != null) {
                                            onDeepLinkFound(id)
                                            status = "QR OK • opening song…"
                                        } else {
                                            status = "Invalid song ID"
                                        }
                                    } else {
                                        status = "Unrecognized QR"
                                    }
                                }
                            }

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner, selector, preview, analysis
                            )
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Text(
                    text = status,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = WhiteText
                )
            }
        } else {
            Text(
                text = "Camera permission required",
                modifier = Modifier.padding(16.dp),
                color = WhiteText
            )
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
private fun processImageProxy(
    imageProxy: ImageProxy,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onQrCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    if (barcode.valueType == Barcode.TYPE_TEXT && barcode.rawValue != null) {
                        onQrCodeScanned(barcode.rawValue!!)
                        break
                    }
                }
            }
            .addOnFailureListener {
                // Ignore scan errors
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
