package com.micmr0.androidcommons

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.launch

abstract class RemoteConfigActivity : ComponentActivity() {
    private var isAdMobInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d(
                    RemoteConfigActivity::class.java.simpleName,
                    "remoteConfig -> fetchAndActivate -> task.isSuccessful: ${task.isSuccessful}"
                )
                Log.d(
                    RemoteConfigActivity::class.java.simpleName,
                    "remoteConfig -> fetchAndActivate -> Config params updated: $updated"
                )
            } else {
                Log.d(
                    RemoteConfigActivity::class.java.simpleName,
                    "remoteConfig -> fetchAndActivate -> Fetch failed"
                )
            }
            val remoteData = fetchRemoteData()
            onRemoteDataFetched(remoteData)
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(
                    RemoteConfigActivity::class.java.simpleName,
                    "Updated keys: " + configUpdate.updatedKeys
                )

                if (configUpdate.updatedKeys.contains(WELCOME_MESSAGE_KEY)
                    || configUpdate.updatedKeys.contains(SHOW_AD_1_KEY)
                    || configUpdate.updatedKeys.contains(SHOW_AD_2_KEY)) {
                    remoteConfig.activate().addOnCompleteListener {
                        val remoteData = fetchRemoteData()
                        onRemoteDataFetched(remoteData)
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(
                    RemoteConfigActivity::class.java.simpleName,
                    "Config update error with code: " + error.code,
                    error
                )
            }
        })
    }

    fun fetchRemoteData(): Map<String, Any> {
        val remoteConfig = Firebase.remoteConfig

        val welcomeMessage = remoteConfig[WELCOME_MESSAGE_KEY].asString()
        val showAd1 = remoteConfig[SHOW_AD_1_KEY].asBoolean()
        val showAd2 = remoteConfig[SHOW_AD_2_KEY].asBoolean()

        Log.d(this::class.java.simpleName, "welcome_message: $welcomeMessage")

        return mapOf(
            WELCOME_MESSAGE_KEY to welcomeMessage,
            SHOW_AD_1_KEY to showAd1,
            SHOW_AD_2_KEY to showAd2
        )

    }
    abstract fun onAdMobInitialized()

    abstract fun onRemoteDataFetched(data: Map<String, Any>)

    protected fun initializeAdMob() {
        if (isAdMobInitialized) {
            Log.d("AdMob", "AdMob already initialized, skipping")
            return
        }

        MobileAds.initialize(this) { initializationStatus ->
            Log.d("AdMob", "AdMob initialized")
            isAdMobInitialized = true
            onAdMobInitialized()
        }
    }

    protected fun requestConsent() {
        val params = if (BuildConfig.DEBUG) {
            val consentDebugSettings = ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("6C126EED7B1DFC9AAC1B8BD7E0EFDDD1")
                .addTestDeviceHashedId("52D435078A7657CF75A4F2695C01EBDE")
                .build()
            ConsentRequestParameters.Builder()
                .setConsentDebugSettings(consentDebugSettings)
                .build()
        } else {
            ConsentRequestParameters.Builder().build()
        }

        val consentInformation = UserMessagingPlatform.getConsentInformation(this)

        // Check, if consent confirmed
        if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED ||
            consentInformation.consentStatus == ConsentInformation.ConsentStatus.UNKNOWN) {

            consentInformation.requestConsentInfoUpdate(this, params, {
                if (consentInformation.isConsentFormAvailable) {
                    UserMessagingPlatform.loadConsentForm(this, { consentForm ->
                        consentForm.show(this) { _ ->
                            lifecycleScope.launch {
                                setConsentGiven()
                            }
                            initializeAdMob()
                        }
                    }, { error ->
                        Log.e("UMP", "Consent form load failed: $error")
                        initializeAdMob()
                    })
                } else {
                    Log.d("UMP", "Consent form not available, proceeding with consent status: ${consentInformation.consentStatus}")
                    initializeAdMob()
                }
            }, { error ->
                Log.e("UMP", "Consent info update failed: $error")
                initializeAdMob()
            })

        } else {
            // ✅ Zgoda już udzielona — nie pokazuj formularza
            Log.d("UMP", "Consent already handled, status: ${consentInformation.consentStatus}")
            initializeAdMob()
        }
    }

    abstract fun shouldRequestConsent(): Boolean

    abstract fun setConsentGiven()

    companion object {
        //REMOTE CONFIG
        const val WELCOME_MESSAGE_KEY = "welcome_message"
        const val SHOW_AD_1_KEY = "show_ad_1"
        const val SHOW_AD_2_KEY = "show_ad_2"
    }
}