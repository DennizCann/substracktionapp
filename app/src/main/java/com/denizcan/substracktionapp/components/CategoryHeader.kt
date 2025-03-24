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
                SubscriptionCategory.STREAMING -> "category_streaming"
                SubscriptionCategory.MUSIC -> "category_music"
                SubscriptionCategory.AI_TOOLS -> "category_ai_tools"
                SubscriptionCategory.SOFTWARE_DEV -> "category_software_dev"
                SubscriptionCategory.CLOUD_STORAGE -> "category_cloud_storage"
                SubscriptionCategory.GAMING -> "category_gaming"
                SubscriptionCategory.EDUCATION -> "category_education"
                SubscriptionCategory.FITNESS -> "category_fitness"
                SubscriptionCategory.NEWS -> "category_news"
                SubscriptionCategory.DESIGN -> "category_design"
                SubscriptionCategory.PRODUCTIVITY -> "category_productivity"
                SubscriptionCategory.SECURITY -> "category_security"
                SubscriptionCategory.SHOPPING -> "category_shopping"
                SubscriptionCategory.SOCIAL_MEDIA -> "category_social_media"
                SubscriptionCategory.FOOD_DELIVERY -> "category_food_delivery"
                SubscriptionCategory.READING -> "category_reading"
                SubscriptionCategory.COMMUNICATION -> "category_communication"
                SubscriptionCategory.OTHER -> "category_other"
            }.localized(currentLanguage),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
} 