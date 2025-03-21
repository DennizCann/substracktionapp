package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.components.CommonTopBar
import com.denizcan.substracktionapp.navigation.Screen
import com.denizcan.substracktionapp.util.localized
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    currentLanguage: String
) {
    var hasSubscriptions by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showPaymentDetails by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "payment_calendar".localized(currentLanguage),
                navController = navController
            )
        }
    ) { paddingValues ->
        if (!hasSubscriptions) {
            // Boş durum gösterimi
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "no_payments".localized(currentLanguage),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "add_subscription_for_calendar".localized(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        navController.navigate(Screen.Subscriptions.route)
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("add_subscription".localized(currentLanguage))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Takvim komponenti buraya gelecek
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Seçili gün için ödemeler
                selectedDate?.let { date ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "payment_details".localized(currentLanguage),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Ödeme detayları buraya gelecek
                        }
                    }
                }
            }
        }
    }

    if (showPaymentDetails) {
        AlertDialog(
            onDismissRequest = { showPaymentDetails = false },
            title = { Text("payment_details".localized(currentLanguage)) },
            text = {
                Column {
                    Text("service".localized(currentLanguage) + ": Netflix")
                    Text("amount".localized(currentLanguage) + ": ₺65.99")
                    Text("date".localized(currentLanguage) + ": 15.04.2024")
                }
            },
            confirmButton = {
                TextButton(onClick = { showPaymentDetails = false }) {
                    Text("close".localized(currentLanguage))
                }
            }
        )
    }
} 