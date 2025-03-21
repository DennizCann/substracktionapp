package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    navController: NavController,
    currentLanguage: String
) {
    var hasSubscriptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "analytics".localized(currentLanguage),
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
                    Icons.Default.Analytics,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "no_data".localized(currentLanguage),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "add_subscription_for_analytics".localized(currentLanguage),
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Özet kartları
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SummaryCard(
                            title = "total_subscriptions".localized(currentLanguage),
                            value = "5",
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "monthly_cost".localized(currentLanguage),
                            value = "₺150",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Grafik kartı
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "payment_distribution".localized(currentLanguage),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Grafik komponenti buraya gelecek
                        }
                    }
                }

                // Son ödemeler
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "last_payments".localized(currentLanguage),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                TextButton(onClick = { /* Tümünü gör */ }) {
                                    Text("view_all".localized(currentLanguage))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // Ödeme listesi buraya gelecek
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
} 