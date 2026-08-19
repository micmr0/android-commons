package com.micmr0.androidcommons

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

fun Context.createLocalizedContext(language: String): Context {
    val locale = Locale.forLanguageTag(language.replace("-r", "-"))

    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)

    return createConfigurationContext(configuration)
}