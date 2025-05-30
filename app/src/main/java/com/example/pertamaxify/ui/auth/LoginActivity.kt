package com.example.pertamaxify.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.R
import com.example.pertamaxify.data.local.SecurePrefs
import com.example.pertamaxify.ui.main.HomeActivity
import com.example.pertamaxify.ui.theme.GreenButton
import com.example.pertamaxify.ui.theme.InputBackground
import com.example.pertamaxify.ui.theme.InputBorder
import com.example.pertamaxify.ui.theme.PertamaxifyTheme
import com.example.pertamaxify.ui.theme.Typography
import com.example.pertamaxify.ui.theme.WhiteHint
import com.example.pertamaxify.ui.theme.WhiteText
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private var isConnected by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PertamaxifyTheme {
                val errorMessage = viewModel.errorMessage

                LoginPage(
                    errorMessage = errorMessage,
                    isLoading = viewModel.isLoading,
                    onLoginClick = { username, password ->
                        if (isConnected) {
                            if (username == "guest") {
                                Log.d("LoginActivity", "Logging in as guest")
                                navigateToHome()
                            } else {
                                viewModel.login(
                                    this,
                                    username,
                                    password,
                                    onSuccess = { accessToken, refreshToken ->
                                        SecurePrefs.saveTokens(this, accessToken, refreshToken)
                                        navigateToHome()
                                    })
                            }
                        } else {
                            Log.d("LoginActivity", "No connection, logging in offline")
                            navigateToHome()
                        }
                    })
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
    errorMessage: String?, isLoading: Boolean, onLoginClick: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .widthIn(max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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

            TextFieldWithLabel(
                label = "Username", text = username, onValueChange = { username = it })

            Spacer(modifier = Modifier.height(20.dp))

            TextFieldWithLabel(
                label = "Password",
                text = password,
                onValueChange = { password = it },
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                onDone = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        onLoginClick(username, password)
                    }
                })

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onLoginClick(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenButton, contentColor = WhiteText
                ),
                shape = RoundedCornerShape(48.dp),
                enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), color = Color.White
                    )
                } else {
                    Text(text = "Log In", color = WhiteText, style = Typography.titleMedium)
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    style = Typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onLoginClick("guest", "")
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                    containerColor = InputBorder, contentColor = WhiteText
                ), shape = RoundedCornerShape(48.dp), enabled = !isLoading
            ) {
                Text(text = "Login as Guest", color = WhiteText, style = Typography.titleMedium)
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
    onTogglePasswordVisibility: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
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
                .padding(horizontal = 3.dp)
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
                singleLine = true,
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
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onDone?.invoke() }),
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

