package com.micmr0.androidcommons

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

fun goToGooglePlay(context: Context) {
    val packageName = context.packageName

    val intent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$packageName".toUri()
        )
        context.startActivity(webIntent)
    }
}

fun showPrivacyPolicy(context: Context, privacyPolicyUrl : String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = privacyPolicyUrl.toUri()
    }
    context.startActivity(intent)
}

fun shareApp(context: Context) {
    val packageName = context.packageName
    val link = "https://play.google.com/store/apps/details?id=$packageName"

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, link)
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.select_an_app_to_share)
        )
    )
}

fun showMoreApps(context: Context, developerName : String) {
    val uri = "https://play.google.com/store/apps/developer?id=$developerName".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.android.vending")
    context.startActivity(intent)

}

inline fun <reified T> loadAndParseJson(context: Context, fileName: String): List<T> {
    val jsonString = context.assets.open(fileName).bufferedReader().use(BufferedReader::readText)
    val type = object : TypeToken<List<T>>() {}.type
    return Gson().fromJson(jsonString, type)
}

@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
}
