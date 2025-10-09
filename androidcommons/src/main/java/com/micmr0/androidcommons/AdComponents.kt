package com.micmr0.androidcommons

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import kotlin.jvm.java

@Composable
fun NativeAdViewComposable(adUnitId: String, isSystemDarkTheme: Boolean) {
    val context = LocalContext.current
    val adView = remember { mutableStateOf<View?>(null) }

    LaunchedEffect(Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.native_ad_layout, null)
        loadNativeAd(context, adUnitId, view, isSystemDarkTheme) {
            adView.value = view
        }
    }

    adView.value?.let { view ->
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { view }
        )
    }
}

private fun loadNativeAd(
    context: Context,
    adUnitId: String,
    adView: View,
    isDarkTheme: Boolean,
    onAdLoaded: () -> Unit
) {
    Log.d("AdMob", "Loading native ad with Ad Unit ID: $adUnitId")

    val consentInformation = UserMessagingPlatform.getConsentInformation(context)
    val adRequestBuilder = AdRequest.Builder()
    if (consentInformation.consentStatus != ConsentInformation.ConsentStatus.OBTAINED) {
        val extras = Bundle()
        extras.putString("npa", "1") // ads not-personalized
        adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        Log.d(
            "AdMob",
            "Using non-personalized ads due to consent status: ${consentInformation.consentStatus}"
        )
    } else {
        Log.d("AdMob", "Using personalized ads")
    }

    val adLoader = AdLoader.Builder(context, adUnitId)
        .forNativeAd { nativeAd ->
            Log.d("AdMob", "Native ad loaded successfully")
            populateNativeAdView(nativeAd, adView as NativeAdView, isDarkTheme)
            onAdLoaded()
        }
        .withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(
                    "AdMob",
                    "Native ad failed to load: ${adError.message}, code: ${adError.code}"
                )
            }
        })
        .withNativeAdOptions(
            NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
                .build()
        )
        .build()

    adLoader.loadAd(adRequestBuilder.build())
}

private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView, isDarkTheme: Boolean) {
    // initialize headline
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    (adView.headlineView as? TextView)?.text = nativeAd.headline
    (adView.headlineView as? TextView)?.setTextColor(
        if (isDarkTheme) Color.WHITE else Color.BLACK
    )

    // initialize text
    adView.bodyView = adView.findViewById(R.id.ad_body)
    (adView.bodyView as? TextView)?.text = nativeAd.body
    (adView.bodyView as? TextView)?.setTextColor(
        if (isDarkTheme) Color.WHITE else Color.BLACK
    )

    // initialize image
    adView.mediaView = adView.findViewById(R.id.ad_image)
    adView.mediaView?.mediaContent = nativeAd.mediaContent

    // initialize call-to-action
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    (adView.callToActionView as? Button)?.text = nativeAd.callToAction
    (adView.callToActionView as? Button)?.setTextColor(
        if (isDarkTheme) Color.WHITE else Color.BLACK
    )

    // set ad in NativeAdView
    adView.setNativeAd(nativeAd)
}