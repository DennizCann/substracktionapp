package com.denizcan.substracktionapp.model

import java.util.Date

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val planType: PlanType = PlanType.FREE,
    val subscriptionCount: Int = 0,
    val planStartDate: Date? = null,
    val planEndDate: Date? = null,
    val isTrialActive: Boolean = false
) 