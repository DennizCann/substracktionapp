package com.denizcan.substracktionapp.model

enum class PlanType {
    FREE,
    PREMIUM
}

data class SubscriptionPlan(
    val type: PlanType,
    val maxSubscriptions: Int?,  // null ise sınırsız
    val monthlyPrice: Double,
    val yearlyPrice: Double,
    val features: List<String>
)

object PlanData {
    fun getPlans(currentLanguage: String): List<SubscriptionPlan> {
        return listOf(
            SubscriptionPlan(
                type = PlanType.FREE,
                maxSubscriptions = 10,
                monthlyPrice = 0.0,
                yearlyPrice = 0.0,
                features = if (currentLanguage == "tr") {
                    listOf(
                        "10 üyelik ekleme hakkı",
                        "Temel bildirimler",
                        "Basit analitikler",
                        "Takvim görünümü"
                    )
                } else {
                    listOf(
                        "Add up to 10 subscriptions",
                        "Basic notifications",
                        "Simple analytics",
                        "Calendar view"
                    )
                }
            ),
            SubscriptionPlan(
                type = PlanType.PREMIUM,
                maxSubscriptions = null,
                monthlyPrice = 29.99,
                yearlyPrice = 299.99,
                features = if (currentLanguage == "tr") {
                    listOf(
                        "Sınırsız üyelik ekleme",
                        "Gelişmiş analitikler",
                        "Özelleştirilebilir kategoriler",
                        "Veri yedekleme",
                        "Aile üyeliği paylaşımı",
                        "Reklamsız deneyim"
                    )
                } else {
                    listOf(
                        "Unlimited subscriptions",
                        "Advanced analytics",
                        "Custom categories",
                        "Data backup",
                        "Family sharing",
                        "Ad-free experience"
                    )
                }
            )
        )
    }
} 