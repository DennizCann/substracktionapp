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
import com.google.firebase.firestore.FirebaseFirestore
import com.denizcan.substracktionapp.util.localized
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.platform.LocalContext
import com.denizcan.substracktionapp.components.CommonTopBar

@OptIn(ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        if (dataStoreRepository.isRememberMeEnabled().first()) {
            rememberMe = true
            email = dataStoreRepository.getSavedEmail().first()
            password = dataStoreRepository.getSavedPassword().first()
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "continue_with_email".localized(currentLanguage),
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                    if (email.isEmpty() || password.isEmpty()) {
                        errorMessage = "please_fill_all".localized(currentLanguage)
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            // Kullanıcı başarıyla giriş yaptıktan sonra Firestore kontrolü
                            scope.launch {
                                try {
                                    val userDoc = db.collection("users")
                                        .document(authResult.user!!.uid)
                                        .get()
                                        .await()

                                    if (!userDoc.exists() || userDoc.getBoolean("profileCompleted") != true) {
                                        navController.navigate(Screen.UserInfo.route) {
                                            popUpTo(Screen.LoginOptions.route) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.LoginOptions.route) { inclusive = true }
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Firestore'a erişimde hata olursa güvenli tarafta kal ve UserInfo'ya yönlendir
                                    navController.navigate(Screen.UserInfo.route) {
                                        popUpTo(Screen.LoginOptions.route) { inclusive = true }
                                    }
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            errorMessage = e.localizedMessage
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
} 