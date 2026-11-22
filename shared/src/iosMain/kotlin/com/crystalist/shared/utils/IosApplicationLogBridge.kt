package com.crystalist.shared.utils

fun logIosApplicationError(
    tag: String,
    message: String,
) {
    Logger.e(tag, message, null)
}
