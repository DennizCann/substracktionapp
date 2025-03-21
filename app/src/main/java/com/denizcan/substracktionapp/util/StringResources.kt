package com.denizcan.substracktionapp.util

object StringResources {
    fun get(key: String, language: String): String {
        return when (language) {
            "tr" -> trStrings[key] ?: key
            else -> enStrings[key] ?: key
        }
    }

    private val trStrings = mapOf(
        // Genel
        "app_name" to "SubsTracktion",
        "continue" to "Devam Et",
        "cancel" to "İptal",
        "save" to "Kaydet",
        "edit" to "Düzenle",
        
        // Navigation
        "home" to "Ana Sayfa",
        "profile" to "Profil",
        "settings" to "Ayarlar",
        "subscriptions" to "Üyelikler",
        "analytics" to "Analiz",
        "calendar" to "Takvim",
        
        // Settings
        "language_settings" to "Dil Ayarları",
        "app_language" to "Uygulama Dili",
        "select_language" to "Dil Seçin",
        "change" to "Değiştir",
        
        // Profile
        "profile_info" to "Profil Bilgileri",
        "welcome_message" to "Hoş geldiniz! Başlamadan önce birkaç bilgiye ihtiyacımız var.",
        "your_name" to "İsminiz",
        "your_country" to "Ülkeniz",
        "country_info" to "Ülke Bilgileri",
        "please_fill_all" to "Lütfen tüm alanları doldurun",
        
        // Notifications
        "notifications" to "Bildirimler",
        "no_notifications" to "Bildirim bulunmamaktadır",
        
        // Auth
        "sign_out" to "Çıkış Yap",
        
        // Error Messages
        "error_saving" to "Bilgiler kaydedilirken bir hata oluştu",
        
        // Subscriptions
        "add_subscription" to "Üyelik Ekle",
        "no_subscriptions" to "Henüz üyelik eklenmemiş",
        "add_first_subscription" to "İlk üyeliğinizi ekleyin",
        "subscription_name" to "Üyelik Adı",
        "subscription_price" to "Ücret",
        "subscription_period" to "Ödeme Periyodu",
        "monthly" to "Aylık",
        "yearly" to "Yıllık",
        "next_payment" to "Sonraki Ödeme",
        "payment_date" to "Ödeme Tarihi",
        "select_service" to "Servis Seçin",
        "custom" to "Özel",
        "delete" to "Sil",
        "confirm_delete" to "Silmek istediğinizden emin misiniz?",
        "yes" to "Evet",
        "no" to "Hayır",
        
        // Analytics
        "total_subscriptions" to "Toplam Üyelikler",
        "monthly_cost" to "Aylık Maliyet",
        "yearly_cost" to "Yıllık Maliyet",
        "most_expensive" to "En Pahalı Üyelik",
        "payment_distribution" to "Ödeme Dağılımı",
        "subscription_types" to "Üyelik Türleri",
        "payment_history" to "Ödeme Geçmişi",
        "no_data" to "Henüz veri bulunmamaktadır",
        "add_subscription_for_analytics" to "Analiz için üyelik ekleyin",
        "last_payments" to "Son Ödemeler",
        "upcoming_payments" to "Yaklaşan Ödemeler",
        "view_all" to "Tümünü Gör",
        
        // Calendar
        "payment_calendar" to "Ödeme Takvimi",
        "no_payments" to "Ödeme bulunmamaktadır",
        "add_subscription_for_calendar" to "Takvimde görüntülemek için üyelik ekleyin",
        "today" to "Bugün",
        "this_month" to "Bu Ay",
        "next_month" to "Gelecek Ay",
        "payment_details" to "Ödeme Detayları",
        "amount" to "Tutar",
        "service" to "Servis",
        "date" to "Tarih"
    )

    private val enStrings = mapOf(
        // General
        "app_name" to "SubsTracktion",
        "continue" to "Continue",
        "cancel" to "Cancel",
        "save" to "Save",
        "edit" to "Edit",
        
        // Navigation
        "home" to "Home",
        "profile" to "Profile",
        "settings" to "Settings",
        "subscriptions" to "Subscriptions",
        "analytics" to "Analytics",
        "calendar" to "Calendar",
        
        // Settings
        "language_settings" to "Language Settings",
        "app_language" to "App Language",
        "select_language" to "Select Language",
        "change" to "Change",
        
        // Profile
        "profile_info" to "Profile Information",
        "welcome_message" to "Welcome! We need some information before we start.",
        "your_name" to "Your Name",
        "your_country" to "Your Country",
        "country_info" to "Country Information",
        "please_fill_all" to "Please fill in all fields",
        
        // Notifications
        "notifications" to "Notifications",
        "no_notifications" to "No notifications",
        
        // Auth
        "sign_out" to "Sign Out",
        
        // Error Messages
        "error_saving" to "Error saving information",
        
        // Subscriptions
        "add_subscription" to "Add Subscription",
        "no_subscriptions" to "No subscriptions yet",
        "add_first_subscription" to "Add your first subscription",
        "subscription_name" to "Subscription Name",
        "subscription_price" to "Price",
        "subscription_period" to "Payment Period",
        "monthly" to "Monthly",
        "yearly" to "Yearly",
        "next_payment" to "Next Payment",
        "payment_date" to "Payment Date",
        "select_service" to "Select Service",
        "custom" to "Custom",
        "delete" to "Delete",
        "confirm_delete" to "Are you sure you want to delete?",
        "yes" to "Yes",
        "no" to "No",
        
        // Analytics
        "total_subscriptions" to "Total Subscriptions",
        "monthly_cost" to "Monthly Cost",
        "yearly_cost" to "Yearly Cost",
        "most_expensive" to "Most Expensive",
        "payment_distribution" to "Payment Distribution",
        "subscription_types" to "Subscription Types",
        "payment_history" to "Payment History",
        "no_data" to "No data available",
        "add_subscription_for_analytics" to "Add subscriptions for analytics",
        "last_payments" to "Last Payments",
        "upcoming_payments" to "Upcoming Payments",
        "view_all" to "View All",
        
        // Calendar
        "payment_calendar" to "Payment Calendar",
        "no_payments" to "No payments",
        "add_subscription_for_calendar" to "Add subscriptions to view in calendar",
        "today" to "Today",
        "this_month" to "This Month",
        "next_month" to "Next Month",
        "payment_details" to "Payment Details",
        "amount" to "Amount",
        "service" to "Service",
        "date" to "Date"
    )
} 