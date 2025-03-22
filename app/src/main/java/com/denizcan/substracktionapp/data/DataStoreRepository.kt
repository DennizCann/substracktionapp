package com.denizcan.substracktionapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class DataStoreRepository(private val context: Context) {
    
    private val isIntroShownKey = booleanPreferencesKey("is_intro_shown")
    private val selectedLanguageKey = stringPreferencesKey("selected_language")
    private val savedEmailKey = stringPreferencesKey("saved_email")
    private val savedPasswordKey = stringPreferencesKey("saved_password")
    private val rememberMeKey = booleanPreferencesKey("remember_me")

    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val COUNTRY = stringPreferencesKey("country")
        val CURRENCY = stringPreferencesKey("currency")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    suspend fun saveIntroShown() {
        context.dataStore.edit { preferences ->
            preferences[isIntroShownKey] = true
        }
    }

    fun isIntroShown(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[isIntroShownKey] ?: false
        }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[selectedLanguageKey] = language
        }
    }

    fun getSelectedLanguage(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[selectedLanguageKey] ?: "en"
        }
    }

    suspend fun saveLoginCredentials(email: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[savedEmailKey] = email
            preferences[savedPasswordKey] = password
            preferences[rememberMeKey] = true
        }
    }

    suspend fun clearLoginCredentials() {
        context.dataStore.edit { preferences ->
            preferences.remove(savedEmailKey)
            preferences.remove(savedPasswordKey)
            preferences[rememberMeKey] = false
        }
    }

    fun getSavedEmail(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[savedEmailKey] ?: ""
        }
    }

    fun getSavedPassword(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[savedPasswordKey] ?: ""
        }
    }

    fun isRememberMeEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[rememberMeKey] ?: false
        }
    }

    suspend fun updateCountryInfo(country: String, currency: String, currencySymbol: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COUNTRY] = country
            preferences[PreferencesKeys.CURRENCY] = currency
            preferences[PreferencesKeys.CURRENCY_SYMBOL] = currencySymbol
        }
    }

    fun getCountry() = context.dataStore.data.map { it[PreferencesKeys.COUNTRY] ?: "TR" }
    fun getCurrency() = context.dataStore.data.map { it[PreferencesKeys.CURRENCY] ?: "TRY" }
    fun getCurrencySymbol() = context.dataStore.data.map { it[PreferencesKeys.CURRENCY_SYMBOL] ?: "₺" }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    fun getNotificationsEnabled() = context.dataStore.data.map { 
        it[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: false 
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    fun getThemeMode() = context.dataStore.data.map { 
        it[PreferencesKeys.THEME_MODE] ?: "system" 
    }
} 