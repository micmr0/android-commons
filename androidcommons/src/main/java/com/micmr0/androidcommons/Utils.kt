package com.micmr0.androidcommons

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

fun goToGooglePlay(context: Context) {
    val packageName = context.packageName

    val marketIntent =
        Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    try {
        if (marketIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(marketIntent)
        } else {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (webIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(webIntent)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.open_google_play_no_app), Toast.LENGTH_LONG
                ).show()
            }
        }
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.open_google_play_problem), Toast.LENGTH_LONG
        ).show()
    }
}

fun showPrivacyPolicy(context: Context, privacyPolicyUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = privacyPolicyUrl.toUri()
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.privacy_policy_no_app),
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.privacy_policy_problem),
            Toast.LENGTH_LONG
        ).show()
    }
}

fun shareApp(context: Context) {
    val packageName = context.packageName
    val link = "https://play.google.com/store/apps/details?id=$packageName"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, link)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.select_an_app_to_share)
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.share_app_no_app),
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.share_app_problem),
            Toast.LENGTH_LONG
        ).show()
    }
}

fun showMoreApps(context: Context, developerName: String) {
    val marketUri = "https://play.google.com/store/apps/developer?id=$developerName".toUri()

    val marketIntent = Intent(Intent.ACTION_VIEW, marketUri).apply {
        setPackage("com.android.vending")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        if (marketIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(marketIntent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, marketUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (webIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(webIntent)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.more_apps_no_app),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.more_apps_problem),
            Toast.LENGTH_LONG
        ).show()
    }
}

fun sendFeedback(context: Context, mail: String) {
    val sendToIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:$mail".toUri()
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback))
    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback))
    }

    try {
        if (sendToIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(sendToIntent)
        } else {
            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.choose_mail_app)))
        }
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.no_mail_app_info),
            Toast.LENGTH_LONG
        ).show()
    }
}

inline fun <reified T> loadAndParseJson(context: Context, fileName: String): List<T> {
    val jsonString = context.assets.open(fileName).bufferedReader().use(BufferedReader::readText)
    val type = object : TypeToken<List<T>>() {}.type
    return Gson().fromJson(jsonString, type)
}

@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
}
