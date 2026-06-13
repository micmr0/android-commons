package com.micmr0.androidcommons

import android.util.Log

inline fun <reified T> T.logv(message: String) =
    logInternal(LogLevel.VERBOSE, T::class.java.simpleName, message)

inline fun <reified T> T.logd(message: String) =
    logInternal(LogLevel.DEBUG, T::class.java.simpleName, message)

inline fun <reified T> T.logi(message: String) =
    logInternal(LogLevel.INFO, T::class.java.simpleName, message)

inline fun <reified T> T.logw(message: String) =
    logInternal(LogLevel.WARN, T::class.java.simpleName, message)

inline fun <reified T> T.loge(message: String) =
    logInternal(LogLevel.ERROR, T::class.java.simpleName, message)

fun logInternal(level: LogLevel, tag: String, message: String) {
    if (!LoggerConfig.enabled) return
    if (level.ordinal < LoggerConfig.minLevel.ordinal) return

    when (level) {
        LogLevel.VERBOSE -> Log.v(tag, message)
        LogLevel.DEBUG -> Log.d(tag, message)
        LogLevel.INFO -> Log.i(tag, message)
        LogLevel.WARN -> Log.w(tag, message)
        LogLevel.ERROR -> Log.e(tag, message)
    }
}