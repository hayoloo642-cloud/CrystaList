package com.crystalist.shared.ui.menu

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.db.deleteUploadedSong
import com.crystalist.shared.generated.resources.Res
import com.crystalist.shared.generated.resources.action_cannot_be_undone
import com.crystalist.shared.generated.resources.cancel
import com.crystalist.shared.generated.resources.delete
import com.crystalist.shared.generated.resources.delete_failed
import com.crystalist.shared.generated.resources.delete_upload
import com.crystalist.shared.generated.resources.delete_upload_question
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.isUploadedType
import com.crystalist.shared.state.GlobalYouTubeRepository
import com.crystalist.shared.ui.components.AlertDialog
import com.crystalist.shared.ui.components.PlayerMenuAction
import com.crystalist.shared.ui.icons.CrystaListIcons
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun rememberDeleteUploadAction(
    song: SongItem,
    database: MusicDatabase,
    onDismiss: () -> Unit,
): PlayerMenuAction? {
    val stored by remember(database, song.id) { database.dao().song(song.id) }.collectAsState(null)
    val repository = GlobalYouTubeRepository.instance
    if (song.isLocal || stored?.song?.isLocal == true ||
        (
            !song.isUploaded && song.uploadEntityId == null && !isUploadedType(song.musicVideoType) &&
                stored?.song?.isUploaded != true && stored?.song?.uploadEntityId == null
        ) || !repository.canManagePlaylists()
    ) {
        return null
    }
    val scope = rememberCoroutineScope()
    var confirming by remember(song.id) { mutableStateOf(false) }
    var deleting by remember(song.id) { mutableStateOf(false) }
    var failed by remember(song.id) { mutableStateOf(false) }
    if (confirming) {
        AlertDialog(
            onDismissRequest = { if (!deleting) confirming = false },
            title = { Text(stringResource(Res.string.delete_upload_question)) },
            text = { Text(stringResource(if (failed) Res.string.delete_failed else Res.string.action_cannot_be_undone)) },
            confirmButton = {
                TextButton(
                    enabled = !deleting,
                    shapes = ButtonDefaults.shapes(shape = RoundedCornerShape(8.dp)),
                    onClick = {
                        deleting = true
                        scope.launch {
                            try {
                                if (database.deleteUploadedSong(song, repository)) {
                                    confirming = false
                                    onDismiss()
                                } else {
                                    failed = true
                                }
                            } catch (error: CancellationException) {
                                throw error
                            } catch (_: Exception) {
                                failed = true
                            } finally {
                                deleting = false
                            }
                        }
                    },
                ) { Text(stringResource(Res.string.delete)) }
            },
            dismissButton = {
                TextButton(enabled = !deleting, onClick = { confirming = false }, shapes = ButtonDefaults.shapes(shape = RoundedCornerShape(8.dp))) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }
    return PlayerMenuAction(CrystaListIcons.Outlined.Delete, stringResource(Res.string.delete_upload)) {
        failed = false
        confirming = true
    }
}
