package com.crystalist.shared.backup

import com.crystalist.shared.constants.ArtistNameAliasesJsonKey
import com.crystalist.shared.settings.ArtistNameAliases
import com.crystalist.shared.settings.PortableSetting
import com.crystalist.shared.settings.PortableSettings
import com.crystalist.shared.settings.SettingType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun PortableSettings?.withLegacyArtistNameAliases(serialized: String): PortableSettings {
    val aliases = ArtistNameAliases.deserialize(serialized)
    val settings = this ?: PortableSettings(entries = emptyList())
    return settings.copy(
        entries =
            settings.entries.filterNot { it.key == ArtistNameAliasesJsonKey } +
                PortableSetting(ArtistNameAliasesJsonKey, SettingType.STRING, Json.encodeToString(aliases)),
    )
}
