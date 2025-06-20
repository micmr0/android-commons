package com.micmr0.androidcommons

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig

abstract class RemoteConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d(
                    RemoteConfigActivity::class.java.simpleName,
                    "remoteConfig -> fetchAndActivate-> task.isSuccessful: ${task.isSuccessful}"
                )
                Log.d(
                    RemoteConfigActivity::class.java.simpleName,
                    "remoteConfig -> fetchAndActivate-> Config params updated: $updated"
                )
            } else {
                Log.d(
                    RemoteConfigActivity::class.java.simpleName,
                    "remoteConfig -> fetchAndActivate-> Fetch failed"
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

    abstract fun onRemoteDataFetched(data: Map<String, Any>)

    companion object {
        //REMOTE CONFIG
        const val WELCOME_MESSAGE_KEY = "welcome_message"
        const val SHOW_AD_1_KEY = "show_ad_1"
        const val SHOW_AD_2_KEY = "show_ad_2"
    }
}