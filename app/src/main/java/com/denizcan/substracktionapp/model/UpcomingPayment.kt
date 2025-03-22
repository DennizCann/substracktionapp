package com.denizcan.substracktionapp.model

import org.threeten.bp.LocalDate

data class UpcomingPayment(
    val subscription: Subscription,
    val date: LocalDate
) 