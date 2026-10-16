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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun NativeAdViewComposable(
    nativeAd: NativeAd,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val nativeAdView = remember {
        LayoutInflater.from(context)
            .inflate(R.layout.native_ad_layout, null) as NativeAdView
    }

    LaunchedEffect(nativeAd, contentColor) {
        populateNativeAdView(
            nativeAd = nativeAd,
            adView = nativeAdView,
            contentColor = contentColor
        )
        Log.d("AdMob", "NativeAdViewComposable: ad bound to view")
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { nativeAdView },
        update = { }
    )
}

private fun loadNativeAd(
    context: Context,
    adUnitId: String,
    adView: View,
    contentColor: Color,
    onAdLoaded: (NativeAd) -> Unit
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
            populateNativeAdView(nativeAd, adView as NativeAdView, contentColor)
            onAdLoaded(nativeAd)
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
                .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_ANY)
                .build()
        )
        .build()

    adLoader.loadAd(adRequestBuilder.build())
}

@Composable
fun rememberNativeAd(
    adUnitId: String,
    isDark: Boolean
): NativeAd? {
    val context = LocalContext.current
    var ad by remember { mutableStateOf<NativeAd?>(null) }

    LaunchedEffect(adUnitId, isDark) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { loadedAd ->
                ad = loadedAd
                Log.d("AdMob", "Native ad loaded once")
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("AdMob", "Failed to load native ad: ${error.message}")
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    return ad
}

private fun populateNativeAdView(
    nativeAd: NativeAd,
    adView: NativeAdView,
    contentColor: Color
) {
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    (adView.headlineView as? TextView)?.apply {
        text = nativeAd.headline
        setTextColor(contentColor.toArgb())
    }

    adView.bodyView = adView.findViewById(R.id.ad_body)
    (adView.bodyView as? TextView)?.apply {
        text = nativeAd.body
        setTextColor(contentColor.toArgb())
    }

    adView.mediaView = adView.findViewById(R.id.ad_media)
    adView.mediaView?.mediaContent = nativeAd.mediaContent

    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    (adView.callToActionView as? Button)?.apply {
        text = nativeAd.callToAction
        setTextColor(contentColor.toArgb())
    }

    adView.setNativeAd(nativeAd)
}