package com.micmr0.androidcommons.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

abstract class BaseSettingsRepository(
    protected val dataStore: DataStore<Preferences>
) {
    protected suspend fun <T> saveValue(key: Preferences.Key<T>, value: T) {
        withContext(Dispatchers.IO) {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    this[key] = value
                }
            }
        }
    }

    protected fun <T> getValueFlow(key: Preferences.Key<T>, default: T): Flow<T> {
        return dataStore.data.map { preferences -> preferences[key] ?: default }
    }

    protected suspend fun <T> getValue(key: Preferences.Key<T>, default: T): T {
        return dataStore.data.map { preferences -> preferences[key] ?: default }.first()
    }

    protected suspend fun clearKey(key: Preferences.Key<*>) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences -> preferences.remove(key) }
        }
    }

    protected suspend inline fun <reified T> decodeJsonList(key: Preferences.Key<String>): List<T> {
        val json = getValue(key, "[]")
        return Json.decodeFromString(json)
    }

    protected suspend inline fun <reified T> encodeAndSaveList(
        key: Preferences.Key<String>,
        list: List<T>
    ) {
        val json = Json.encodeToString(list)
        saveValue(key, json)
    }


    fun wasOnboardingDisplayed(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_DISPLAYED_KEY] ?: false
        }
    }

    suspend fun setOnboardingDisplayed(displayed: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    this[PreferencesKeys.ONBOARDING_DISPLAYED_KEY] = displayed
                }
            }
        }
    }

    private fun getAppLaunchedCount(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.APP_LAUNCHED_COUNT_KEY] ?: 0
        }
    }

    private suspend fun setAppLaunchedCount(count: Int) {
        withContext(Dispatchers.IO) {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    this[PreferencesKeys.APP_LAUNCHED_COUNT_KEY] = count
                }
            }
        }
    }

    private fun getLastAppShown(): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.LAST_APP_SHOWN_KEY] ?: 0
        }
    }

    private suspend fun setLastAppShown(currentTimeMillis: Long) {
        dataStore.data.map {
            withContext(Dispatchers.IO) {
                dataStore.updateData { preferences ->
                    preferences.toMutablePreferences().apply {
                        this[PreferencesKeys.LAST_APP_SHOWN_KEY] = currentTimeMillis
                    }
                }
            }
        }
    }

    fun isConsentGiven(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.CONSENT_GIVEN_KEY] ?: false
        }
    }

    suspend fun setConsentGiven(given: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    this[PreferencesKeys.CONSENT_GIVEN_KEY] = given
                }
            }
        }
    }

    private fun isShowRateDialogEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SHOW_RATE_APP_KEY] ?: true
        }
    }

    suspend fun setShowRateAppDialog(show: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    this[PreferencesKeys.SHOW_RATE_APP_KEY] = show
                }
            }
        }
    }

    suspend fun showRateAppDialog(): Boolean {
        val canShow: Boolean

        if (isShowRateDialogEnabled().first()) {
            val launchThreshold = 3
            val timeThreshold = 24 * 60 * 60 * 1000 //  (24h)

            val launchCount = getAppLaunchedCount().first() + 1
            val lastShown = getLastAppShown().first()

            val currentTime = System.currentTimeMillis()

            canShow = launchCount >= launchThreshold && (currentTime - lastShown) >= timeThreshold

            setAppLaunchedCount(if (canShow) 0 else launchCount)
            if (canShow) setLastAppShown(currentTime)
        } else {
            canShow = false
        }
        return canShow
    }

    object PreferencesKeys {
        val ONBOARDING_DISPLAYED_KEY = booleanPreferencesKey("show_onboarding")
        val SHOW_RATE_APP_KEY = booleanPreferencesKey("show_rate_app")
        val APP_LAUNCHED_COUNT_KEY = intPreferencesKey("app_launch_count")
        val LAST_APP_SHOWN_KEY = longPreferencesKey("last_app_shown")
        val CONSENT_GIVEN_KEY = booleanPreferencesKey("consent_given")
    }
}