package com.crystalist.shared.network

import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap

object PublicArtworkDns : Dns {
    private const val CACHE_TTL_MS = 10 * 60 * 1000L

    private class Entry(
        val addresses: List<InetAddress>,
        val expiresAt: Long,
    )

    private val cache = ConcurrentHashMap<String, Entry>()

    override fun lookup(hostname: String): List<InetAddress> {
        val now = System.currentTimeMillis()
        cache[hostname]?.let { entry ->
            if (now < entry.expiresAt) return entry.addresses
            cache.remove(hostname)
        }
        // Artwork grids resolve the same handful of CDN hosts constantly, so keep
        // the validated result for a short window instead of asking the resolver
        // on every thumbnail request.
        val addresses = artworkAddresses(hostname, Dns.SYSTEM.lookup(hostname))
        cache[hostname] = Entry(addresses, now + CACHE_TTL_MS)
        if (cache.size > 256) {
            val cutoff = now
            cache.entries.removeIf { it.value.expiresAt <= cutoff }
        }
        return addresses
    }
}

internal fun artworkAddresses(
    hostname: String,
    addresses: List<InetAddress>,
): List<InetAddress> {
    val host = hostname.lowercase().removeSuffix(".")
    val isYouTubeArtworkHost = host.endsWith(".googleusercontent.com") || host.endsWith(".ggpht.com") || host.endsWith(".ytimg.com")
    // VPN DNS can map official CDN hosts to private addresses. HTTPS still verifies their certificates.
    if (addresses.isEmpty() ||
        (!isYouTubeArtworkHost && addresses.any { !it.isPublicArtworkAddress() })
    ) {
        throw UnknownHostException("Artwork host does not resolve exclusively to public addresses")
    }
    return addresses
}

internal fun InetAddress.isPublicArtworkAddress(): Boolean {
    if (isAnyLocalAddress || isLoopbackAddress || isLinkLocalAddress || isSiteLocalAddress || isMulticastAddress) {
        return false
    }
    val bytes = address.map(Byte::toInt).map { it and 0xff }
    return when (bytes.size) {
        4 -> {
            when {
                bytes[0] == 0 || bytes[0] == 10 || bytes[0] == 127 || bytes[0] >= 224 -> false
                bytes[0] == 100 && bytes[1] in 64..127 -> false
                bytes[0] == 169 && bytes[1] == 254 -> false
                bytes[0] == 172 && bytes[1] in 16..31 -> false
                bytes[0] == 192 && bytes[1] == 168 -> false
                (bytes[0] == 192 && bytes[1] == 0 && (bytes[2] == 0 || bytes[2] == 2)) -> false
                (bytes[0] == 198 && (bytes[1] in 18..19 || (bytes[1] == 51 && bytes[2] == 100))) -> false
                bytes[0] == 203 && bytes[1] == 0 && bytes[2] == 113 -> false
                else -> true
            }
        }

        16 -> {
            !(
                (bytes[0] and 0xfe == 0xfc) ||
                    (bytes[0] == 0x20 && bytes[1] == 0x01 && bytes[2] == 0x0d && bytes[3] == 0xb8)
            )
        }

        else -> {
            false
        }
    }
}
