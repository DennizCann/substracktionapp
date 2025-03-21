package com.denizcan.substracktionapp.navigation

sealed class Screen(val route: String) {
    object Intro : Screen("intro")
    object LoginOptions : Screen("login_options")
    object EmailLogin : Screen("email_login")
    object EmailSignup : Screen("email_signup")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Subscriptions : Screen("subscriptions")
    object Analytics : Screen("analytics")
    object Calendar : Screen("calendar")
    object Settings : Screen("settings")
    object UserInfo : Screen("user_info")
} 