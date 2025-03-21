package com.denizcan.substracktionapp.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.MainActivity
import com.denizcan.substracktionapp.auth.GoogleAuthUiClient
import com.denizcan.substracktionapp.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun LoginOptionsScreen(
    navController: NavController,
    currentLanguage: String,
    googleAuthUiClient: GoogleAuthUiClient
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { navController.navigate(Screen.EmailLogin.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = if (currentLanguage == "tr") "E-posta ile Giriş" else "Sign in with Email"
            )
        }

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                try {
                    val signInIntent = googleAuthUiClient.getSignInIntent()
                    (context as Activity).startActivityForResult(
                        signInIntent,
                        MainActivity.REQ_GOOGLE_SIGN_IN
                    )
                } catch (e: Exception) {
                    errorMessage = if (currentLanguage == "tr") {
                        "Google ile giriş başarısız: ${e.localizedMessage}"
                    } else {
                        "Google sign in failed: ${e.localizedMessage}"
                    }
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = if (currentLanguage == "tr") "Google ile Giriş" else "Sign in with Google"
                )
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
} 