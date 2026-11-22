package com.crystalist.shared.utils

import kotlin.time.Clock

object CrystaListClock {
    fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()
}
