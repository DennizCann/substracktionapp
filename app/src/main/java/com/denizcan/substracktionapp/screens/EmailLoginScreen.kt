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
import com.denizcan.substracktionapp.data.DataStoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun EmailLoginScreen(
    navController: NavController,
    currentLanguage: String,
    dataStoreRepository: DataStoreRepository
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var rememberMe by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (dataStoreRepository.isRememberMeEnabled().first()) {
            rememberMe = true
            email = dataStoreRepository.getSavedEmail().first()
            password = dataStoreRepository.getSavedPassword().first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (currentLanguage == "tr") "E-posta ile Giriş" else "Sign in with Email",
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text(
                text = if (currentLanguage == "tr") "Beni Hatırla" else "Remember Me",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    if (email.isEmpty()) {
                        errorMessage = if (currentLanguage == "tr") {
                            "Lütfen e-posta adresinizi girin"
                        } else {
                            "Please enter your email address"
                        }
                        return@TextButton
                    }
                    
                    isLoading = true
                    errorMessage = null
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            errorMessage = if (currentLanguage == "tr") {
                                "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi."
                            } else {
                                "Password reset link has been sent to your email."
                            }
                            isLoading = false
                        }
                        .addOnFailureListener { exception ->
                            errorMessage = if (currentLanguage == "tr") {
                                "Şifre sıfırlama e-postası gönderilemedi: ${exception.localizedMessage}"
                            } else {
                                "Failed to send password reset email: ${exception.localizedMessage}"
                            }
                            isLoading = false
                        }
                }
            ) {
                Text(
                    text = if (currentLanguage == "tr") "Şifremi Unuttum" else "Forgot Password",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
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

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user?.isEmailVerified == true) {
                            scope.launch {
                                if (rememberMe) {
                                    dataStoreRepository.saveLoginCredentials(email, password)
                                } else {
                                    dataStoreRepository.clearLoginCredentials()
                                }
                            }
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.LoginOptions.route) { inclusive = true }
                            }
                        } else {
                            // Email onaylanmamış
                            FirebaseAuth.getInstance().signOut()
                            errorMessage = if (currentLanguage == "tr") {
                                "Lütfen önce e-posta adresinizi onaylayın. Onay e-postası gönderildi."
                            } else {
                                "Please verify your email first. Verification email has been sent."
                            }
                            // Yeni onay maili gönder
                            user?.sendEmailVerification()
                            isLoading = false
                        }
                    }
                    .addOnFailureListener { exception ->
                        errorMessage = if (currentLanguage == "tr") {
                            "Giriş başarısız: ${exception.localizedMessage}"
                        } else {
                            "Login failed: ${exception.localizedMessage}"
                        }
                        isLoading = false
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (currentLanguage == "tr") "Giriş Yap" else "Sign In")
            }
        }

        TextButton(
            onClick = { navController.navigate(Screen.EmailSignup.route) }
        ) {
            Text(
                if (currentLanguage == "tr") 
                "Hesabınız yok mu? Kayıt olun" 
                else 
                "Don't have an account? Sign up"
            )
        }
    }
} 