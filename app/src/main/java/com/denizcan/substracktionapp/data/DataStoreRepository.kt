package com.denizcan.substracktionapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreRepository(private val context: Context) {
    
    private val isIntroShownKey = booleanPreferencesKey("is_intro_shown")
    private val selectedLanguageKey = stringPreferencesKey("selected_language")
    private val savedEmailKey = stringPreferencesKey("saved_email")
    private val savedPasswordKey = stringPreferencesKey("saved_password")
    private val rememberMeKey = booleanPreferencesKey("remember_me")

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
} 