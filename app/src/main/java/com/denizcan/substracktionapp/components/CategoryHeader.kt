package com.denizcan.substracktionapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denizcan.substracktionapp.model.SubscriptionCategory
import com.denizcan.substracktionapp.util.localized

@Composable
fun CategoryHeader(
    category: SubscriptionCategory,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (category) {
                SubscriptionCategory.ENTERTAINMENT -> "category_entertainment"
                SubscriptionCategory.SHOPPING -> "category_shopping"
                SubscriptionCategory.PRODUCTIVITY -> "category_productivity"
                SubscriptionCategory.CLOUD -> "category_cloud"
                SubscriptionCategory.EDUCATION -> "category_education"
                SubscriptionCategory.HEALTH -> "category_health"
                SubscriptionCategory.GAMING -> "category_gaming"
                SubscriptionCategory.FINANCE -> "category_finance"
                SubscriptionCategory.COMMUNICATION -> "category_communication"
                SubscriptionCategory.OTHER -> "category_other"
            }.localized(currentLanguage),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
} 