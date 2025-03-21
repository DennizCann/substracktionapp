package com.denizcan.substracktionapp.model

import com.google.firebase.Timestamp
import java.util.*

data class Subscription(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val category: SubscriptionCategory = SubscriptionCategory.OTHER,
    val billingPeriod: BillingPeriod = BillingPeriod.MONTHLY,
    val color: Int? = null,
    val isActive: Boolean = true,
    val paymentDay: Int = 1,
    val startDate: Timestamp? = null,
    val nextPaymentDate: Timestamp = Timestamp.now()
)

enum class BillingPeriod {
    WEEKLY,     // Haftalık
    MONTHLY,    // Aylık
    QUARTERLY,  // 3 Aylık
    BIANNUALLY, // 6 Aylık
    YEARLY      // Yıllık
}

enum class SubscriptionCategory {
    ENTERTAINMENT,    // Netflix, Spotify, Disney+, YouTube Premium
    SHOPPING,        // Amazon Prime, Trendyol Premium
    PRODUCTIVITY,    // Microsoft 365, Adobe CC, Notion
    CLOUD,          // iCloud, Google One, Dropbox
    EDUCATION,      // Udemy, Coursera, Duolingo
    HEALTH,         // Fitness apps, meditation apps
    GAMING,         // PlayStation Plus, Xbox Game Pass, Steam
    FINANCE,        // Trading apps, budgeting apps
    COMMUNICATION,  // Zoom, Slack premium
    OTHER
} 