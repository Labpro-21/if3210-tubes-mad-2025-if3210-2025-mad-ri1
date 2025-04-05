package com.example.pertamaxify.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pertamaxify.R
import com.example.pertamaxify.ui.theme.*

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PertamaxifyTheme {
                LoginPage()
            }
        }
    }
}

@Composable
fun LoginPage() {
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
            // Logo Image
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )

            // Text Below Logo
            Text(
                text = "Millions of Songs.\nOnly on Purritify.",
                style = Typography.displayMedium,
                modifier = Modifier.padding(top = 12.dp),
                color = WhiteText,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email Label
            Text(
                text = "Email",
                color = WhiteText,
                style = Typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 4.dp)
            )

            // Email Input Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, InputBorder, RoundedCornerShape(6.dp))
                    .background(InputBackground, RoundedCornerShape(6.dp))
                    .padding(horizontal = 3.dp, vertical = 0.dp)
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", style = Typography.labelSmall, color = WhiteHint) },
                    textStyle = Typography.labelSmall.copy(color = WhiteText),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp),
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

            Spacer(modifier = Modifier.height(20.dp))

            // Password Label
            Text(
                text = "Password",
                color = WhiteText,
                style = Typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 4.dp)
            )

            // Password Input Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, InputBorder, RoundedCornerShape(6.dp))
                    .background(InputBackground, RoundedCornerShape(6.dp))
                    .padding(horizontal = 3.dp, vertical = 0.dp)
            ) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", style = Typography.labelSmall, color = WhiteHint) },
                    textStyle = Typography.labelSmall.copy(color = WhiteText),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = if (passwordVisible) R.drawable.eye_show else R.drawable.eye_hide),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = WhiteText
                            )
                        }
                    },
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

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            Button(
                onClick = {
                    Log.d("LoginButton", "Login button clicked") // This should appear in Logcat
                },
                modifier = Modifier
                    .width(327.dp)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenButton,
                    contentColor = WhiteText
                ),
                shape = RoundedCornerShape(48.dp),
                enabled = true// Button is disabled if empty
            ) {
                Text(
                    text = "Log In",
                    color = WhiteText,
                    style = Typography.titleMedium
                )
            }

        }
    }
}

@Preview
@Composable
fun LoginPagePreview() {
    PertamaxifyTheme {
        LoginPage()
    }
}
