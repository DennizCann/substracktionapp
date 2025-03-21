package com.denizcan.substracktionapp.model

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Date,
    val isRead: Boolean = false,
    val type: NotificationType
)

enum class NotificationType {
    PAYMENT_DUE,      // Yaklaşan ödeme bildirimi
    PRICE_CHANGE,     // Fiyat değişikliği bildirimi
    SUBSCRIPTION_END  // Abonelik bitiş bildirimi
} 