package com.crystalist.shared.network

import okhttp3.OkHttpClient

// InnerTube traffic is POST-based, so an HTTP disk cache would not help and
// could persist account-scoped responses. Keep the shared pooled client.
internal actual fun apiOkHttpClient(): OkHttpClient = sharedOkHttpClient
