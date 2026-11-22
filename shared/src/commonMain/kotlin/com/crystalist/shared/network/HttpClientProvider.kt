package com.crystalist.shared.network

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
