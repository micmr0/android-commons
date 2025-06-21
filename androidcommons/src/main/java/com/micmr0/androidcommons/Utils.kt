package com.micmr0.androidcommons

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.BufferedReader

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
