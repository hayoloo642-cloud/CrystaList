package com.crystalist.shared.ui.components.lyrics

import com.crystalist.shared.model.LyricsEntry
import com.crystalist.shared.model.WordTimestamp

/**
 * Derives approximate per-word timings for lines that only carry a line timestamp.
 *
 * Providers such as Lrclib and KuGou usually ship line-level LRC only. Without
 * word data the Apple-style word animation never runs, so this distributes the
 * line's duration across its words by character count.
 *
 * This is an estimate, not real vocal alignment: the highlight advances evenly
 * through the line rather than on the sung syllable.
 *
 * Only call this from the UI layer. The derived words are deliberately marked
 * `isLineTimed = false` so the word animation runs, and the repository keeps
 * storing the original line-level data, so caching and "word synced" detection
 * stay honest.
 */
internal fun List<LyricsEntry>.withEstimatedWordTimings(): List<LyricsEntry> {
    if (isEmpty()) return this
    val hasRealWordTiming =
        any { entry -> entry.words.orEmpty().any { !it.isLineTimed && it.endTime > it.startTime } }
    if (hasRealWordTiming) return this

    return mapIndexed { index, entry ->
        if (entry.words?.any { !it.isLineTimed } == true) return@mapIndexed entry
        val text = entry.text
        if (text.isBlank()) return@mapIndexed entry

        val startSec = entry.time / 1000.0
        val nextStartSec =
            listOfNotNull(
                this.getOrNull(index + 1)?.time?.takeIf { it > entry.time }?.div(1000.0),
                entry.endTime?.takeIf { it > entry.time }?.div(1000.0),
            ).minOrNull()
        val endSec =
            nextStartSec
                ?: entry.endTime?.div(1000.0)?.takeIf { it > startSec }
                // Last line without an end: assume a sung line of about four seconds.
                ?: (startSec + 4.0)
        if (endSec <= startSec) return@mapIndexed entry

        val derived = buildEstimatedWords(text, startSec, endSec)
        if (derived.isEmpty()) entry else entry.copy(words = derived)
    }
}

private fun buildEstimatedWords(
    text: String,
    startSec: Double,
    endSec: Double,
): List<WordTimestamp> {
    val tokens = text.split(' ').filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return emptyList()

    val totalWeight = tokens.sumOf { it.length }.coerceAtLeast(1)
    val duration = endSec - startSec
    var consumed = 0
    return tokens.mapIndexed { index, token ->
        val weight = token.length.toDouble() / totalWeight
        val wordStart = startSec + duration * consumed
        consumed += token.length
        val wordEnd = startSec + duration * consumed
        WordTimestamp(
            text = token,
            startTime = wordStart,
            endTime = if (index == tokens.lastIndex) endSec else wordEnd,
            hasTrailingSpace = index != tokens.lastIndex,
            isBackground = false,
            // Must be false or the word animation gate rejects these words.
            isLineTimed = false,
        )
    }
}
