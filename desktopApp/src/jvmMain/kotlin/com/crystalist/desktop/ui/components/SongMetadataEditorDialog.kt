package com.crystalist.desktop.ui.components

import androidx.compose.runtime.Composable
import com.crystalist.desktop.state.app.AppState
import com.crystalist.shared.db.MusicDatabase
import com.crystalist.shared.model.SongItem
import com.crystalist.shared.model.editSongMetadata
import com.crystalist.shared.model.resetSongMetadata
import com.crystalist.shared.state.SongMetadataState
import com.crystalist.shared.ui.menu.EditSongDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.koin.core.context.GlobalContext

@Composable
internal fun SongMetadataEditorDialog(
    song: SongItem,
    onSaved: () -> Unit,
    onDismiss: () -> Unit,
) {
    val effectiveSong = SongMetadataState.apply(song)
    EditSongDialog(
        title = effectiveSong.title,
        artist =
            effectiveSong.artists
                .firstOrNull()
                ?.name
                .orEmpty(),
        onSave = { title, artist ->
            AppState.scope
                .async(Dispatchers.IO) {
                    editSongMetadata(GlobalContext.get().get<MusicDatabase>(), song, title, artist)
                }.await()
        },
        onSaved = onSaved,
        onDismiss = onDismiss,
        onReset = {
            AppState.scope
                .async(Dispatchers.IO) {
                    resetSongMetadata(GlobalContext.get().get<MusicDatabase>(), song.id)
                }.await()
        },
    )
}
