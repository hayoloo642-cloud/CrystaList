/*
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.shared.api.lyrics.romanizer

import com.crystalist.shared.utils.XdgUtils
import java.io.File

internal actual fun glyphmindStorageDirectory(): String = File(XdgUtils.getXdgDataHome(), "crystalist/glyphmind").absolutePath

internal actual fun removeLegacyJapaneseDictionary() {
    File(XdgUtils.getXdgDataHome(), "crystalist/japanese-dictionary").deleteRecursively()
}
