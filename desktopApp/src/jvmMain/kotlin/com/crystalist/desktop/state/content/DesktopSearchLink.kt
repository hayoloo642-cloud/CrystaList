package com.crystalist.desktop.state.content

import com.metrolist.innertubex.utils.YouTubeUrlParser
import com.crystalist.shared.utils.parseYouTubePodcastId
import com.crystalist.shared.utils.parseYouTubeUrl

internal sealed interface DesktopSearchLink {
    data class Podcast(
        val id: String,
    ) : DesktopSearchLink

    data class Media(
        val destination: YouTubeUrlParser.ParsedUrl,
    ) : DesktopSearchLink
}

internal fun resolveDesktopSearchLink(query: String): DesktopSearchLink? =
    parseYouTubePodcastId(query)?.let(DesktopSearchLink::Podcast)
        ?: parseYouTubeUrl(query)?.let(DesktopSearchLink::Media)
