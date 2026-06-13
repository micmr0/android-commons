package com.micmr0.androidcommons

object LoggerConfig {
    var enabled: Boolean = false
    var minLevel: LogLevel = LogLevel.DEBUG
}

enum class LogLevel { VERBOSE, DEBUG, INFO, WARN, ERROR }
