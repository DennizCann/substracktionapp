package com.denizcan.substracktionapp.util

import com.denizcan.substracktionapp.model.BillingPeriod
import com.denizcan.substracktionapp.model.Subscription
import com.denizcan.substracktionapp.model.UpcomingPayment
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun calculateUpcomingPayments(subscriptions: List<Subscription>): List<UpcomingPayment> {
    val today = LocalDate.now()
    val oneMonthLater = today.plusMonths(1)
    
    return subscriptions
        .filter { it.isActive }
        .flatMap<Subscription, UpcomingPayment> { subscription ->
            generateSequence<LocalDate>(
                subscription.startDate?.let { timestamp ->
                    Instant.ofEpochMilli(timestamp.toDate().time)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                } ?: today
            ) { date ->
                when (subscription.billingPeriod) {
                    BillingPeriod.WEEKLY -> date.plusWeeks(1)
                    BillingPeriod.MONTHLY -> date.plusMonths(1)
                    BillingPeriod.QUARTERLY -> date.plusMonths(3)
                    BillingPeriod.BIANNUALLY -> date.plusMonths(6)
                    BillingPeriod.YEARLY -> date.plusYears(1)
                }
            }
            .takeWhile { it <= oneMonthLater }
            .filter { it >= today }
            .map { UpcomingPayment(subscription, it) }
        }
        .sortedBy { it.date }
}

fun formatPaymentDate(date: LocalDate, currentLanguage: String): String {
    val today = LocalDate.now()
    val pattern = when (currentLanguage) {
        "tr" -> "d MMMM"
        else -> "MMMM d"
    }
    val yearPattern = when (currentLanguage) {
        "tr" -> "d MMMM yyyy"
        else -> "MMMM d, yyyy"
    }
    val locale = when (currentLanguage) {
        "tr" -> Locale("tr", "TR")
        else -> Locale.ENGLISH
    }

    return when {
        date == today -> "today".localized(currentLanguage)
        date == today.plusDays(1) -> "tomorrow".localized(currentLanguage)
        date.year == today.year -> date.format(DateTimeFormatter.ofPattern(pattern, locale))
        else -> date.format(DateTimeFormatter.ofPattern(yearPattern, locale))
    }
} 