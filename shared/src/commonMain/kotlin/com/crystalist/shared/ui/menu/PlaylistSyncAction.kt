package com.crystalist.shared.ui.menu

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.db.refreshStoredPlaylist
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.error_occurred
import com.crystalist.shared.generated.resources.ok
import com.crystalist.shared.generated.resources.sync_playlist
import com.crystalist.shared.model.PlaylistItem
import com.crystalist.shared.model.isSyncablePlaylistId
import com.crystalist.shared.state.GlobalYouTubeRepository
import com.crystalist.shared.ui.components.AlertDialog
import com.crystalist.shared.ui.components.PlayerMenuAction
import com.crystalist.shared.ui.components.PopupVisibility
import com.crystalist.shared.ui.icons.CrystaListIcons
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun rememberPlaylistSyncAction(
    playlist: PlaylistItem,
    onDismiss: () -> Unit,
): PlayerMenuAction? {
    if (playlist.isLocal || !playlist.id.isSyncablePlaylistId()) return null
    val scope = rememberCoroutineScope()
    var syncing by remember(playlist.id) { mutableStateOf(false) }
    var failed by remember(playlist.id) { mutableStateOf(false) }
    val label = stringResource(Res.string.sync_playlist)
    PopupVisibility(failed) {
        AlertDialog(
            onDismissRequest = { failed = false },
            title = { Text(label) },
            text = { Text(stringResource(Res.string.error_occurred)) },
            confirmButton = {
                TextButton(onClick = { failed = false }, shapes = ButtonDefaults.shapes(shape = RoundedCornerShape(8.dp))) {
                    Text(stringResource(Res.string.ok))
                }
            },
        )
    }
    return PlayerMenuAction(
        icon = CrystaListIcons.Outlined.Sync,
        title = label,
        isLoading = syncing,
        enabled = !syncing,
        onClick = {
            if (!syncing) {
                syncing = true
                scope.launch {
                    try {
                        withContext(Dispatchers.Default) {
                            KoinPlatform.getKoin().get<MusicDatabase>().refreshStoredPlaylist(GlobalYouTubeRepository.instance, playlist.id)
                        }
                        onDismiss()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        failed = true
                    } finally {
                        syncing = false
                    }
                }
            }
        },
    )
}
