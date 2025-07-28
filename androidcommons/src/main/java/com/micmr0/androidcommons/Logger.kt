package com.micmr0.androidcommons

import android.util.Log

inline fun <reified T> T.logv(message: String) {
    if (BuildConfig.DEBUG) Log.v(TAG, message)
}

inline fun <reified T> T.logi(message: String) {
    if (BuildConfig.DEBUG) Log.i(TAG, message)
}

inline fun <reified T> T.logw(message: String) {
    if (BuildConfig.DEBUG) Log.w(TAG, message)
}

inline fun <reified T> T.logd(message: String) {
    if (BuildConfig.DEBUG) Log.d(TAG, message)
}

inline fun <reified T> T.loge(message: String) {
    if (BuildConfig.DEBUG) Log.e(TAG, message)
}

inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName