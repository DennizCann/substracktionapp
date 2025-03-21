package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.components.CommonTopBar
import com.denizcan.substracktionapp.model.BillingPeriod
import com.denizcan.substracktionapp.model.Subscription
import com.denizcan.substracktionapp.model.SubscriptionCategory
import com.denizcan.substracktionapp.util.ColorUtils
import com.denizcan.substracktionapp.util.formatCurrency
import com.denizcan.substracktionapp.util.localized
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    navController: NavController,
    currentLanguage: String
) {
    var subscriptions by remember { mutableStateOf<List<Subscription>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // Üyelikleri yükle
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("subscriptions")
                .whereEqualTo("userId", user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    snapshot?.let { documents ->
                        subscriptions = documents.mapNotNull { doc ->
                            doc.toObject(Subscription::class.java)
                        }
                    }
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "analytics".localized(currentLanguage),
                navController = navController
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (subscriptions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "no_data".localized(currentLanguage),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Toplam Maliyet Kartları
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Aylık Toplam
                    SummaryCard(
                        title = "monthly_cost".localized(currentLanguage),
                        amount = calculateTotalMonthlyCost(subscriptions),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Yıllık Toplam
                    SummaryCard(
                        title = "yearly_cost".localized(currentLanguage),
                        amount = calculateTotalYearlyCost(subscriptions),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Kategorilere Göre Analiz
                Text(
                    text = "category_analysis".localized(currentLanguage),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                SubscriptionCategory.values().forEach { category ->
                    val categorySubscriptions = subscriptions.filter { it.category == category }
                    if (categorySubscriptions.isNotEmpty()) {
                        CategoryAnalysisCard(
                            category = category,
                            subscriptions = categorySubscriptions,
                            currentLanguage = currentLanguage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CategoryAnalysisCard(
    category: SubscriptionCategory,
    subscriptions: List<Subscription>,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    val monthlyCost = calculateTotalMonthlyCost(subscriptions)
    val yearlyCost = calculateTotalYearlyCost(subscriptions)
    val categoryColor = ColorUtils.subscriptionColors[category.ordinal % ColorUtils.subscriptionColors.size]

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "category_${category.name.lowercase()}".localized(currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    color = categoryColor
                )
                Text(
                    text = "${subscriptions.size}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "monthly".localized(currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(monthlyCost),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "yearly".localized(currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(yearlyCost),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

private fun calculateTotalMonthlyCost(subscriptions: List<Subscription>): Double {
    return subscriptions.sumOf { subscription: Subscription ->
        when (subscription.billingPeriod) {
            BillingPeriod.WEEKLY -> subscription.amount * 4.0     // Ayda ortalama 4 hafta
            BillingPeriod.MONTHLY -> subscription.amount
            BillingPeriod.QUARTERLY -> subscription.amount / 3.0
            BillingPeriod.BIANNUALLY -> subscription.amount / 6.0
            BillingPeriod.YEARLY -> subscription.amount / 12.0
        }
    }
}

private fun calculateTotalYearlyCost(subscriptions: List<Subscription>): Double {
    return subscriptions.sumOf { subscription: Subscription ->
        when (subscription.billingPeriod) {
            BillingPeriod.WEEKLY -> subscription.amount * 52.0    // Yılda 52 hafta
            BillingPeriod.MONTHLY -> subscription.amount * 12.0
            BillingPeriod.QUARTERLY -> subscription.amount * 4.0
            BillingPeriod.BIANNUALLY -> subscription.amount * 2.0
            BillingPeriod.YEARLY -> subscription.amount
        }
    }
} 