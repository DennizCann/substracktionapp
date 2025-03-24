package com.denizcan.substracktionapp.model

import com.google.firebase.Timestamp
import java.util.*

data class Subscription(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
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
    STREAMING,       // Netflix, Disney+, BluTV, Amazon Prime Video
    MUSIC,          // Spotify, Apple Music, YouTube Music, Deezer
    AI_TOOLS,       // ChatGPT, Midjourney, Claude
    SOFTWARE_DEV,   // GitHub, JetBrains, Visual Studio
    CLOUD_STORAGE,  // Google One, iCloud, Dropbox
    GAMING,         // PlayStation Plus, Xbox Game Pass, Steam
    EDUCATION,      // Udemy, Coursera, Skillshare
    FITNESS,        // Strava, MyFitnessPal, Nike Training Club
    NEWS,           // NY Times, Bloomberg, Financial Times
    DESIGN,         // Adobe CC, Figma, Canva
    PRODUCTIVITY,   // Microsoft 365, Notion, Evernote
    SECURITY,       // NordVPN, LastPass, Bitdefender
    SHOPPING,       // Amazon Prime, Trendyol Premium
    SOCIAL_MEDIA,   // Twitter Blue, LinkedIn Premium
    FOOD_DELIVERY,  // Yemeksepeti, Getir
    READING,        // Medium, Kindle Unlimited, Storytel
    COMMUNICATION,  // Zoom, Slack, Discord Nitro
    OTHER
} 