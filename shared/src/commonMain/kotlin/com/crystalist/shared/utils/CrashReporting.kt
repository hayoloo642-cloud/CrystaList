package com.crystalist.shared.utils

import com.crystalist.shared.BuildConfig

fun isCrashReportingAvailable(appVersion: String = BuildConfig.APP_VERSION): Boolean = '+' !in appVersion
