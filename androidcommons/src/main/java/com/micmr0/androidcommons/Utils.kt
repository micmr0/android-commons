package com.micmr0.androidcommons

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.BufferedReader

private fun goToGooglePlay(context: Context) {
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
