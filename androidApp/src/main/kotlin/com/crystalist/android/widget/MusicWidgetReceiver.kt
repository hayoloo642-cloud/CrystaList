/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.crystalist.android.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.crystalist.android.App
import com.crystalist.android.playback.MusicService
import com.crystalist.android.utils.launchAsync
import kotlinx.coroutines.CancellationException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MusicWidgetReceiver :
    AppWidgetProvider(),
    KoinComponent {
    private val widgetManager: CrystaListWidgetManager by inject()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        requestWidgetRefresh(context, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        requestWidgetRefresh(context, intArrayOf(appWidgetId))
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray,
    ) {
        requestWidgetRefresh(context, intArrayOf())
    }

    private fun requestWidgetRefresh(
        context: Context,
        appWidgetIds: IntArray,
    ) {
        launchAsync {
            try {
                (context.applicationContext as App).startup.await()
                widgetManager.refreshProgressDemand()
                if (MusicService.isRunning) {
                    requestServiceSnapshot(context)
                } else {
                    widgetManager.updateIdleMusicWidgets(appWidgetIds)
                    if (MusicService.isRunning) requestServiceSnapshot(context)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Timber.e(error, "Failed to refresh music widgets")
            }
        }
    }

    private fun requestServiceSnapshot(context: Context) {
        context.startService(
            Intent(context, MusicService::class.java).setAction(ACTION_UPDATE_WIDGET),
        )
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.crystalist.music.widget.PLAY_PAUSE"
        const val ACTION_LIKE = "com.crystalist.music.widget.LIKE"
        const val ACTION_NEXT = "com.crystalist.music.widget.NEXT"
        const val ACTION_PREVIOUS = "com.crystalist.music.widget.PREVIOUS"
        const val ACTION_UPDATE_WIDGET = "com.crystalist.music.widget.UPDATE_WIDGET"
    }
}
