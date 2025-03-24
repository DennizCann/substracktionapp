package com.denizcan.substracktionapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.denizcan.substracktionapp.data.DataStoreRepository
import com.denizcan.substracktionapp.navigation.Screen
import kotlinx.coroutines.launch
import com.denizcan.substracktionapp.viewmodel.LanguageViewModel
import androidx.navigation.NavController
import com.denizcan.substracktionapp.ui.theme.SubsTracktionAppTheme

@Composable
fun IntroScreen(
    navController: NavController,
    dataStoreRepository: DataStoreRepository,
    languageViewModel: LanguageViewModel
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Dil seçimi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { languageViewModel.setLanguage("tr") }
            ) {
                Text(
                    text = "TR",
                    color = if (currentLanguage == "tr") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
            TextButton(
                onClick = { languageViewModel.setLanguage("en") }
            ) {
                Text(
                    text = "EN",
                    color = if (currentLanguage == "en") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Uygulama başlığı ve özellikleri
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SubsTraction",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Text(
                text = if (currentLanguage == "tr") "Özellikler:" else "Features:",
                style = MaterialTheme.typography.titleMedium
            )
            
            val features = if (currentLanguage == "tr") {
                listOf(
                    "Tüm aboneliklerinizi tek yerden takip edin",
                    "Ödeme tarihlerini takip edin",
                    "Bütçe planlaması yapın",
                    "Abonelik maliyetlerinizi analiz edin",
                    "Hatırlatıcılar alın"
                )
            } else {
                listOf(
                    "Track all your subscriptions in one place",
                    "Monitor payment dates",
                    "Plan your budget",
                    "Analyze subscription costs",
                    "Get reminders"
                )
            }
            
            features.forEach { feature ->
                Text(text = "• $feature")
            }
        }

        // Başla butonu
        Button(
            onClick = {
                scope.launch {
                    dataStoreRepository.saveIntroShown()
                    navController.navigate(Screen.LoginOptions.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(text = if (currentLanguage == "tr") "Başla" else "Start")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    val previewNavController = rememberNavController()
    val previewDataStore = DataStoreRepository(LocalContext.current)
    val previewLanguageViewModel = LanguageViewModel(previewDataStore)
    
    SubsTracktionAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            IntroScreen(
                navController = previewNavController,
                dataStoreRepository = previewDataStore,
                languageViewModel = previewLanguageViewModel
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun IntroScreenDarkPreview() {
    val previewNavController = rememberNavController()
    val previewDataStore = DataStoreRepository(LocalContext.current)
    val previewLanguageViewModel = LanguageViewModel(previewDataStore)
    
    SubsTracktionAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            IntroScreen(
                navController = previewNavController,
                dataStoreRepository = previewDataStore,
                languageViewModel = previewLanguageViewModel
            )
        }
    }
} 