package com.crystalist.shared

import com.crystalist.shared.utils.isCrashReportingAvailable

/** Sideloading changes bundle IDs and drops version suffixes, so neither identifies the release. */
fun iosSentryRelease(): String = "com.crystalist.ios@${BuildConfig.APP_VERSION}"

fun iosSentrySourceRevision(): String = BuildConfig.SOURCE_REVISION

fun iosCrashReportingAvailable(): Boolean = isCrashReportingAvailable()
