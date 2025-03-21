package com.denizcan.substracktionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.denizcan.substracktionapp.data.DataStoreRepository
import com.denizcan.substracktionapp.navigation.Screen
import com.denizcan.substracktionapp.screens.*
import com.denizcan.substracktionapp.ui.theme.SubsTracktionAppTheme
import com.denizcan.substracktionapp.viewmodel.LanguageViewModel
import com.denizcan.substracktionapp.viewmodel.LanguageViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.denizcan.substracktionapp.auth.GoogleAuthUiClient
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var googleAuthUiClient: GoogleAuthUiClient
    private val languageViewModel: LanguageViewModel by viewModels {
        LanguageViewModelFactory(dataStoreRepository)
    }
    private var navController: NavController? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreRepository = DataStoreRepository(applicationContext)
        googleAuthUiClient = GoogleAuthUiClient(applicationContext)
        
        setContent {
            val currentLanguage by languageViewModel.currentLanguage.collectAsState()
            
            SubsTracktionAppTheme {
                val navControllerLocal = rememberNavController()
                navController = navControllerLocal
                
                val currentUser = FirebaseAuth.getInstance().currentUser
                
                // Intro ekranının daha önce gösterilip gösterilmediğini kontrol et
                val startDestination = runBlocking {
                    when {
                        currentUser != null -> {
                            // Kullanıcı profil bilgilerini kontrol et
                            try {
                                val userDoc = FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(currentUser.uid)
                                    .get()
                                    .await()

                                if (!userDoc.exists()) {
                                    // Kullanıcı dökümanı yoksa UserInfo'ya yönlendir
                                    Screen.UserInfo.route
                                } else if (userDoc.getBoolean("profileCompleted") == true) {
                                    // Profil tamamlanmışsa ana sayfaya yönlendir
                                    Screen.Home.route
                                } else {
                                    // Profil tamamlanmamışsa UserInfo'ya yönlendir
                                    Screen.UserInfo.route
                                }
                            } catch (e: Exception) {
                                // Firestore'a erişimde hata olursa güvenli tarafta kal ve UserInfo'ya yönlendir
                                Screen.UserInfo.route
                            }
                        }
                        !dataStoreRepository.isIntroShown().first() -> Screen.Intro.route
                        else -> Screen.LoginOptions.route
                    }
                }
                
                NavHost(
                    navController = navControllerLocal,
                    startDestination = startDestination
                ) {
                    composable(Screen.Intro.route) {
                        IntroScreen(
                            navController = navControllerLocal,
                            dataStoreRepository = dataStoreRepository,
                            languageViewModel = languageViewModel
                        )
                    }
                    composable(Screen.LoginOptions.route) {
                        LoginOptionsScreen(
                            navController = navControllerLocal,
                            currentLanguage = currentLanguage,
                            googleAuthUiClient = googleAuthUiClient
                        )
                    }
                    composable(Screen.EmailLogin.route) {
                        EmailLoginScreen(
                            navController = navControllerLocal,
                            currentLanguage = currentLanguage,
                            dataStoreRepository = dataStoreRepository
                        )
                    }
                    composable(Screen.EmailSignup.route) {
                        EmailSignupScreen(
                            navController = navControllerLocal,
                            currentLanguage = currentLanguage
                        )
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController = navControllerLocal,
                            currentLanguage = currentLanguage
                        )
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(navController = navControllerLocal, currentLanguage = currentLanguage)
                    }
                    composable(Screen.Subscriptions.route) {
                        SubscriptionsScreen(navController = navControllerLocal, currentLanguage = currentLanguage)
                    }
                    composable(Screen.Analytics.route) {
                        AnalyticsScreen(navController = navControllerLocal, currentLanguage = currentLanguage)
                    }
                    composable(Screen.Calendar.route) {
                        CalendarScreen(navController = navControllerLocal, currentLanguage = currentLanguage)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            navController = navControllerLocal,
                            currentLanguage = currentLanguage,
                            dataStoreRepository = dataStoreRepository
                        )
                    }
                    composable(Screen.UserInfo.route) {
                        UserInfoScreen(
                            navController = navControllerLocal,
                            currentLanguage = currentLanguage
                        )
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_GOOGLE_SIGN_IN) {
            try {
                data?.let { intent ->
                    lifecycleScope.launch {
                        val signInResult = googleAuthUiClient.signInWithIntent(intent)
                        if (signInResult.data != null) {
                            // Google ile giriş yapıldığında da profil bilgilerini kontrol et
                            val userDoc = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(signInResult.data.userId)
                                .get()
                                .await()

                            runOnUiThread {
                                if (!userDoc.exists() || userDoc.getBoolean("profileCompleted") != true) {
                                    navController?.navigate(Screen.UserInfo.route) {
                                        popUpTo(Screen.LoginOptions.route) { inclusive = true }
                                    }
                                } else {
                                    navController?.navigate(Screen.Home.route) {
                                        popUpTo(Screen.LoginOptions.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val REQ_GOOGLE_SIGN_IN = 2
    }
}