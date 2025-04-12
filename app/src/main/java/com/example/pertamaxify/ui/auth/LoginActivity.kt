package com.example.pertamaxify.ui.auth

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.R
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.ui.main.HomeActivity
import com.example.pertamaxify.ui.network.NetworkUtils
import com.example.pertamaxify.ui.theme.GreenButton
import com.example.pertamaxify.ui.theme.InputBackground
import com.example.pertamaxify.ui.theme.InputBorder
import com.example.pertamaxify.ui.theme.PertamaxifyTheme
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteHint
import com.example.pertamaxify.ui.theme.WhiteText

class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    private var isConnected by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PertamaxifyTheme {
                // Initialize network monitoring
                LaunchedEffect(Unit) {
                    NetworkUtils.registerNetworkCallback(this@LoginActivity)
                }

                // Observe the network connection state
                NetworkUtils.isConnected.collectAsState().value.let {
                    isConnected = it
                }

                LoginPage(
                    isConnected = isConnected,
                    onLoginClick = { email, password ->
                        if (isConnected) {
                            // Proceed with login using the network
                            viewModel.login(
                                email, password,
                                onSuccess = { accessToken, refreshToken ->
                                    SecurePrefs.saveTokens(this, accessToken, refreshToken)
                                    Log.d(
                                        "LoginActivity",
                                        "Login Successful! Access Token: $accessToken"
                                    )
                                    navigateToHome()
                                },
                                onError = { error ->
                                    Log.e("LoginActivity", "Login Failed: $error")
                                }
                            )
                        } else {
                            Log.d("LoginActivity", "No connection, logging in offline")
                            navigateToHome()
                        }
                    }
                )
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginPage(
    isConnected: Boolean,
    onLoginClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val denied = permissionsMap.filterValues { !it }
        if (denied.isNotEmpty()) {
            Toast.makeText(
                context,
                "Permission denied: ${denied.keys.joinToString()}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissions)
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(360.dp)
                .height(700.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Millions of Songs.\nOnly on Purritify.",
                style = Typography.displayMedium,
                modifier = Modifier.padding(top = 12.dp),
                color = WhiteText,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextFieldWithLabel("Email", email, onValueChange = { email = it })

            Spacer(modifier = Modifier.height(20.dp))

            TextFieldWithLabel(
                label = "Password",
                text = password,
                onValueChange = { password = it },
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .width(327.dp)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenButton,
                    contentColor = WhiteText
                ),
                shape = RoundedCornerShape(48.dp)
            ) {
                Text(text = "Log In", color = WhiteText, style = Typography.titleMedium)
            }

            if (!isConnected) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Connection Detected",
                    color = WhiteText,
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onLoginClick(email, password) },
                    modifier = Modifier.width(327.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InputBorder,
                        contentColor = WhiteText
                    ),
                    shape = RoundedCornerShape(48.dp)
                ) {
                    Text(text = "Login Offline", color = WhiteText, style = Typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun TextFieldWithLabel(
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            color = WhiteText,
            style = Typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, InputBorder, RoundedCornerShape(6.dp))
                .background(InputBackground, RoundedCornerShape(6.dp))
                .padding(horizontal = 3.dp, vertical = 0.dp)
        ) {
            TextField(
                value = text,
                onValueChange = onValueChange,
                placeholder = { Text(label, style = Typography.labelSmall, color = WhiteHint) },
                textStyle = Typography.labelSmall.copy(color = WhiteText),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(6.dp),
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = if (isPassword) {
                    {
                        IconButton(onClick = { onTogglePasswordVisibility?.invoke() }) {
                            Icon(
                                painter = painterResource(id = if (passwordVisible) R.drawable.eye_show else R.drawable.eye_hide),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = WhiteText
                            )
                        }
                    }
                } else null,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = WhiteText,
                    unfocusedTextColor = WhiteText,
                    focusedPlaceholderColor = WhiteHint,
                    unfocusedPlaceholderColor = WhiteHint,
                    focusedContainerColor = InputBackground,
                    unfocusedContainerColor = InputBackground,
                    cursorColor = WhiteText,
                    focusedIndicatorColor = InputBackground,
                    unfocusedIndicatorColor = InputBackground
                )
            )
        }
    }
}
