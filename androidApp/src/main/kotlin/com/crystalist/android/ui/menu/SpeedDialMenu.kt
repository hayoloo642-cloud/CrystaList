package com.crystalist.android.ui.menu

import androidx.compose.runtime.Composable
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.model.YTItem
import com.crystalist.shared.ui.menu.SpeedDialToggle
import org.koin.compose.koinInject
import com.crystalist.shared.ui.menu.rememberSpeedDialToggle as rememberSharedSpeedDialToggle

@Composable
internal fun rememberSpeedDialToggle(item: YTItem): SpeedDialToggle = rememberSharedSpeedDialToggle(item, koinInject<MusicDatabase>())
