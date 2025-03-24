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
        "date" to "Tarih",
        
        // Login Options
        "welcome_title" to "SubsTracktion'a\nHoş Geldiniz",
        "welcome_subtitle" to "Üyeliklerinizi kolayca takip edin,\nmaliyetlerinizi kontrol altında tutun",
        "continue_with_google" to "Google ile Devam Et",
        "continue_with_email" to "E-posta ile Devam Et",
        "or" to "veya",
        
        // Categories
        "category_streaming" to "Dizi & Film",
        "category_music" to "Müzik",
        "category_ai_tools" to "Yapay Zeka",
        "category_software_dev" to "Yazılım Geliştirme",
        "category_cloud_storage" to "Bulut Depolama",
        "category_gaming" to "Oyun",
        "category_education" to "Eğitim",
        "category_fitness" to "Fitness & Sağlık",
        "category_news" to "Haberler",
        "category_design" to "Tasarım",
        "category_productivity" to "Verimlilik",
        "category_security" to "Güvenlik",
        "category_shopping" to "Alışveriş",
        "category_social_media" to "Sosyal Medya",
        "category_food_delivery" to "Yemek & Market",
        "category_reading" to "Okuma & Kitap",
        "category_communication" to "İletişim",
        "category_other" to "Diğer",
        "quarterly" to "3 Aylık",
        "biannually" to "6 Aylık",
        
        "select_category" to "Kategori Seçin",
        "select_color" to "Renk Seçin",
        "delete_subscription_confirm" to "Bu üyeliği silmek istediğinizden emin misiniz?",
        "active_subscriptions" to "Aktif Üyelikler",
        "category_analysis" to "Kategori Analizi",
        "edit_subscription" to "Üyeliği Düzenle",
        "payments_for_date" to "Günün Ödemeleri",
        "no_payments_for_date" to "Bu güne ait ödeme bulunmuyor",
        "weekly" to "Haftalık",
        "start_date" to "Başlangıç Tarihi",
        "ok" to "Tamam",
        "most_expensive_subscription" to "En Pahalı Üyelik",
        "most_expensive_subscriptions" to "En Pahalı Üyelikler",
        "welcome_back" to "Hoş geldin",
        "subscription_summary" to "İşte üyeliklerinin özeti",
        "delete_account" to "Hesabı Sil",
        "delete_account_warning" to "Hesabınızı silmek için tıklayın",
        "delete_account_title" to "Hesabı Silme",
        "delete_account_message" to "Hesabınızı silmek üzeresiniz. Bu işlem geri alınamaz ve tüm verileriniz silinecektir.",
        "delete_account_confirm_title" to "Son Onay",
        "delete_account_confirm_message" to "Hesabınız ve tüm verileriniz kalıcı olarak silinecek. Bu işlem geri alınamaz.",
        "delete_account_confirm_button" to "Hesabımı Sil",
        "close" to "Kapat",
        "no_upcoming_payments" to "Yaklaşan ödeme bulunmuyor",
        "tomorrow" to "Yarın",
        "subscription_limit_reached" to "Üyelik limitine ulaştınız",
        "upgrade_to_premium" to "Premium'a yükseltin",
        "subscription_limit_message" to "Daha fazla üyelik eklemek için Premium'a yükseltin",
        "current_plan" to "Mevcut Plan",
        "free_plan" to "Ücretsiz Plan",
        "premium_plan" to "Premium Plan",
        "save_yearly" to "Yıllık planla %17 tasarruf edin",
        "features_included" to "Dahil Olan Özellikler",
        "upgrade_now" to "Hemen Yükselt",
        "subscription_remaining" to "Kalan üyelik hakkı: %d",
        "current_subscription_count" to "Mevcut üyelik sayınız: %d",
        "notification_reminders" to "Ödeme Hatırlatıcıları",
        "notification_description" to "Yaklaşan ödemeler için bildirim al",
        "premium_feature" to "Premium Özellik",
        "notifications_premium_message" to "Bildirimler premium üyelere özel bir özelliktir. Premium'a yükseltmek ister misiniz?",
        
        // Bildirimler
        "payment_reminder" to "Ödeme Hatırlatıcısı",
        "payment_reminder_message" to "%s için %s ödemeniz yarın",
        
        // TR
        "theme_settings" to "Tema Ayarları",
        "app_theme" to "Uygulama Teması",
        "select_theme" to "Tema Seç",
        "theme_light" to "Açık Tema",
        "theme_dark" to "Koyu Tema",
        "theme_system" to "Sistem Teması",
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
        "date" to "Date",
        
        // Login Options
        "welcome_title" to "Welcome to\nSubsTracktion",
        "welcome_subtitle" to "Easily track your subscriptions,\nkeep your costs under control",
        "continue_with_google" to "Continue with Google",
        "continue_with_email" to "Continue with Email",
        "or" to "or",
        
        // Categories
        "category_streaming" to "Streaming",
        "category_music" to "Music",
        "category_ai_tools" to "AI Tools",
        "category_software_dev" to "Software Development",
        "category_cloud_storage" to "Cloud Storage",
        "category_gaming" to "Gaming",
        "category_education" to "Education",
        "category_fitness" to "Fitness & Health",
        "category_news" to "News",
        "category_design" to "Design",
        "category_productivity" to "Productivity",
        "category_security" to "Security",
        "category_shopping" to "Shopping",
        "category_social_media" to "Social Media",
        "category_food_delivery" to "Food & Grocery",
        "category_reading" to "Reading & Books",
        "category_communication" to "Communication",
        "category_other" to "Other",
        "quarterly" to "Quarterly",
        "biannually" to "Biannually",
        
        "select_category" to "Select Category",
        "select_color" to "Select Color",
        "delete_subscription_confirm" to "Are you sure you want to delete this subscription?",
        "active_subscriptions" to "Active Subscriptions",
        "category_analysis" to "Category Analysis",
        "edit_subscription" to "Edit Subscription",
        "payments_for_date" to "Payments for Date",
        "no_payments_for_date" to "No payments for this date",
        "weekly" to "Weekly",
        "start_date" to "Start Date",
        "ok" to "OK",
        "most_expensive_subscription" to "Most Expensive Subscription",
        "most_expensive_subscriptions" to "Most Expensive Subscriptions",
        "welcome_back" to "Welcome back",
        "subscription_summary" to "Here's your subscription summary",
        "delete_account" to "Delete Account",
        "delete_account_warning" to "Click to delete your account",
        "delete_account_title" to "Delete Account",
        "delete_account_message" to "You are about to delete your account. This action cannot be undone and all your data will be deleted.",
        "delete_account_confirm_title" to "Final Confirmation",
        "delete_account_confirm_message" to "Your account and all your data will be permanently deleted. This action cannot be undone.",
        "delete_account_confirm_button" to "Delete My Account",
        "close" to "Close",
        "no_upcoming_payments" to "No upcoming payments",
        "tomorrow" to "Tomorrow",
        "subscription_limit_reached" to "Subscription limit reached",
        "upgrade_to_premium" to "Upgrade to Premium",
        "subscription_limit_message" to "Upgrade to Premium to add more subscriptions",
        "current_plan" to "Current Plan",
        "free_plan" to "Free Plan",
        "premium_plan" to "Premium Plan",
        "save_yearly" to "Save 17% with yearly plan",
        "features_included" to "Features Included",
        "upgrade_now" to "Upgrade Now",
        "subscription_remaining" to "Subscriptions remaining: %d",
        "current_subscription_count" to "Your current subscription count: %d",
        "notification_reminders" to "Payment Reminders",
        "notification_description" to "Get notified for upcoming payments",
        "premium_feature" to "Premium Feature",
        "notifications_premium_message" to "Notifications are a premium feature. Would you like to upgrade to Premium?",
        
        // Notifications
        "payment_reminder" to "Payment Reminder",
        "payment_reminder_message" to "Your payment of %s for %s is due tomorrow",
        
        // EN
        "theme_settings" to "Theme Settings",
        "app_theme" to "App Theme",
        "select_theme" to "Select Theme",
        "theme_light" to "Light Theme",
        "theme_dark" to "Dark Theme",
        "theme_system" to "System Theme",
    )
} 