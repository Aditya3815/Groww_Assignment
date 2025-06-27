package com.example.groww_assignment.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "stocks_preferences")
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferenceKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val DEFAULT_WATCHLIST_ID = longPreferencesKey("default_watchlist_id")
        val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        val API_CALLS_TODAY = intPreferencesKey("api_calls_today")
        val LAST_API_CALL_DATE = stringPreferencesKey("last_api_call_date")
        val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.IS_DARK_THEME] ?: false
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_DARK_THEME] = isDark
        }
    }

    val defaultWatchlistId: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DEFAULT_WATCHLIST_ID] ?: -1L
    }

    suspend fun setDefaultWatchlistId(id: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DEFAULT_WATCHLIST_ID] = id
        }
    }

    val lastSyncTimestamp: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LAST_SYNC_TIMESTAMP] ?: 0L
    }

    suspend fun updateLastSyncTimestamp() {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_SYNC_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    val apiCallsToday: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.API_CALLS_TODAY] ?: 0
    }

    suspend fun incrementApiCallCount() {
        context.dataStore.edit { preferences ->
            val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            val lastCallDate = preferences[PreferenceKeys.LAST_API_CALL_DATE] ?: ""

            if (lastCallDate != currentDate) {
                // Reset count for new day
                preferences[PreferenceKeys.API_CALLS_TODAY] = 1
                preferences[PreferenceKeys.LAST_API_CALL_DATE] = currentDate
            } else {
                // Increment count for same day
                val currentCount = preferences[PreferenceKeys.API_CALLS_TODAY] ?: 0
                preferences[PreferenceKeys.API_CALLS_TODAY] = currentCount + 1
            }
        }
    }

    val selectedCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SELECTED_CURRENCY] ?: "USD"
    }

    suspend fun setSelectedCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SELECTED_CURRENCY] = currency
        }
    }

    suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}