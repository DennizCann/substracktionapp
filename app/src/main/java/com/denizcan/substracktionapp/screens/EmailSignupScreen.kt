package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EmailSignupScreen(
    navController: NavController,
    currentLanguage: String
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (currentLanguage == "tr") "Hesap Oluştur" else "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(if (currentLanguage == "tr") "E-posta" else "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (currentLanguage == "tr") "Şifre" else "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(if (currentLanguage == "tr") "Şifreyi Onayla" else "Confirm Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = if (currentLanguage == "tr") {
                        "Şifreler eşleşmiyor"
                    } else {
                        "Passwords don't match"
                    }
                    return@Button
                }

                isLoading = true
                errorMessage = null
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                // Başarılı kayıt mesajı göster
                                errorMessage = if (currentLanguage == "tr") {
                                    "Kayıt başarılı! Lütfen e-posta adresinizi onaylayın. Onay e-postası gönderildi."
                                } else {
                                    "Registration successful! Please verify your email. Verification email has been sent."
                                }
                                isLoading = false
                                // 3 saniye sonra giriş sayfasına yönlendir
                                MainScope().launch {
                                    delay(3000)
                                    navController.navigate(Screen.EmailLogin.route) {
                                        popUpTo(Screen.EmailSignup.route) { inclusive = true }
                                    }
                                }
                            }
                            ?.addOnFailureListener { exception ->
                                errorMessage = if (currentLanguage == "tr") {
                                    "Onay e-postası gönderilemedi: ${exception.localizedMessage}"
                                } else {
                                    "Failed to send verification email: ${exception.localizedMessage}"
                                }
                                isLoading = false
                            }
                    }
                    .addOnFailureListener { exception ->
                        errorMessage = if (currentLanguage == "tr") {
                            "Kayıt başarısız: ${exception.localizedMessage}"
                        } else {
                            "Registration failed: ${exception.localizedMessage}"
                        }
                        isLoading = false
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = !isLoading && 
                     email.isNotEmpty() && 
                     password.isNotEmpty() && 
                     confirmPassword.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (currentLanguage == "tr") "Kayıt Ol" else "Sign Up")
            }
        }

        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text(
                if (currentLanguage == "tr") 
                "Zaten hesabınız var mı? Giriş yapın" 
                else 
                "Already have an account? Sign in"
            )
        }
    }
} 