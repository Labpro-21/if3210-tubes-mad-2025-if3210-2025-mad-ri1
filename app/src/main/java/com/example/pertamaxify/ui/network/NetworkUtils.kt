package com.example.pertamaxify.ui.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NetworkUtils {
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    fun registerNetworkCallback(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val builder = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isConnected.value = true
                }

                override fun onLost(network: Network) {
                    _isConnected.value = false
                }

                override fun onUnavailable() {
                    _isConnected.value = false
                }
            }
        )
    }
}
