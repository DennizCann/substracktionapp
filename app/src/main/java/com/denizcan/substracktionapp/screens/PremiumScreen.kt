package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.components.CommonTopBar
import com.denizcan.substracktionapp.model.PlanData
import com.denizcan.substracktionapp.model.PlanType
import com.denizcan.substracktionapp.util.localized
import com.denizcan.substracktionapp.util.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    navController: NavController,
    currentLanguage: String
) {
    var selectedPeriod by remember { mutableStateOf("monthly") }
    val plans = remember(currentLanguage) { PlanData.getPlans(currentLanguage) }
    val premiumPlan = plans.find { it.type == PlanType.PREMIUM }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "premium_plan".localized(currentLanguage),
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Başlık
            Text(
                text = "upgrade_to_premium".localized(currentLanguage),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Fiyatlandırma Seçenekleri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = selectedPeriod == "monthly",
                    onClick = { selectedPeriod = "monthly" },
                    label = { Text("monthly".localized(currentLanguage)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = selectedPeriod == "yearly",
                    onClick = { selectedPeriod = "yearly" },
                    label = { Text("yearly".localized(currentLanguage)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fiyat
            premiumPlan?.let { plan ->
                Text(
                    text = formatCurrency(
                        if (selectedPeriod == "monthly") plan.monthlyPrice 
                        else plan.yearlyPrice
                    ),
                    style = MaterialTheme.typography.displayMedium
                )
                
                if (selectedPeriod == "yearly") {
                    Text(
                        text = "save_yearly".localized(currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Özellikler
            Text(
                text = "features_included".localized(currentLanguage),
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            premiumPlan?.features?.forEach { feature ->
                ListItem(
                    headlineContent = { Text(feature) },
                    leadingContent = {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Satın Al Butonu
            Button(
                onClick = {
                    // Ödeme işlemleri burada yapılacak
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("upgrade_now".localized(currentLanguage))
            }

            // İptal Butonu
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("cancel".localized(currentLanguage))
            }
        }
    }
} 