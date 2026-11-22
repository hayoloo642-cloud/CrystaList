package com.crystalist.shared.network

import okhttp3.OkHttpClient

internal actual fun apiOkHttpClient(): OkHttpClient = sharedOkHttpClient
