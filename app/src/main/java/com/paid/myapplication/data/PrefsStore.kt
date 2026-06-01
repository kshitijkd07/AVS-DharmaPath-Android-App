package com.paid.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "dharma_prefs")

object PrefsKeys {
    val IS_AUTHENTICATED = booleanPreferencesKey("is_authenticated")
    val HAS_ONBOARDED    = booleanPreferencesKey("has_onboarded")
    val USER_NAME        = stringPreferencesKey("user_name")
    val USER_CITY        = stringPreferencesKey("user_city")
    val USER_STATE       = stringPreferencesKey("user_state")
    val USER_DEITY       = stringPreferencesKey("user_deity")
    val REMINDER_TIME    = stringPreferencesKey("reminder_time")
    val THEME            = stringPreferencesKey("theme")
    val JAAP_STATS       = stringPreferencesKey("jaap_stats")
    val JAAP_PREFS       = stringPreferencesKey("jaap_prefs")
}

class PrefsStore(private val context: Context) {

    val isAuthenticated: Flow<Boolean> = context.dataStore.data
        .map { it[PrefsKeys.IS_AUTHENTICATED] ?: false }

    val hasOnboarded: Flow<Boolean> = context.dataStore.data
        .map { it[PrefsKeys.HAS_ONBOARDED] ?: false }

    val theme: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.THEME] ?: "dark" }

    val userName: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.USER_NAME] ?: "Kshitij" }

    val userCity: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.USER_CITY] ?: "Agra" }

    val userState: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.USER_STATE] ?: "Uttar Pradesh" }

    val userDeity: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.USER_DEITY] ?: "Shiva" }

    val jaapStats: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.JAAP_STATS] ?: "" }

    val jaapPrefs: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.JAAP_PREFS] ?: "" }

    suspend fun setAuthenticated(value: Boolean) {
        context.dataStore.edit { it[PrefsKeys.IS_AUTHENTICATED] = value }
    }

    suspend fun setOnboarded(value: Boolean) {
        context.dataStore.edit { it[PrefsKeys.HAS_ONBOARDED] = value }
    }

    suspend fun setTheme(value: String) {
        context.dataStore.edit { it[PrefsKeys.THEME] = value }
    }

    suspend fun saveUserProfile(name: String, city: String, state: String, deity: String) {
        context.dataStore.edit {
            it[PrefsKeys.USER_NAME]  = name
            it[PrefsKeys.USER_CITY]  = city
            it[PrefsKeys.USER_STATE] = state
            it[PrefsKeys.USER_DEITY] = deity
        }
    }

    suspend fun saveJaapStats(json: String) {
        context.dataStore.edit { it[PrefsKeys.JAAP_STATS] = json }
    }

    suspend fun saveJaapPrefs(json: String) {
        context.dataStore.edit { it[PrefsKeys.JAAP_PREFS] = json }
    }
}
