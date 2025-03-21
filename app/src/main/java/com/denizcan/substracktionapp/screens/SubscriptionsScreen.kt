package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.components.CommonTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    navController: NavController,
    currentLanguage: String
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = if (currentLanguage == "tr") "Üyelikler" else "Subscriptions",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (currentLanguage == "tr") 
                    "Üyelikler sayfası yakında geliyor!\n\nBurada tüm aboneliklerinizi yönetebileceksiniz." 
                else 
                    "Subscriptions page coming soon!\n\nHere you will be able to manage all your subscriptions.",
                textAlign = TextAlign.Center
            )
        }
    }
} 