package com.example.pertamaxify.ui.song

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.pertamaxify.data.model.Song
import com.example.pertamaxify.ui.theme.Typography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(song: Song, onLongClick: (Song) -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .combinedClickable(
                onClick = { /* Maybe play song */ },
                onLongClick = { onLongClick(song) }
            )
    ) {
        AsyncImage(
            model = song.imagePath,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(song.title, style = Typography.bodyLarge)
            Text(song.singer, style = Typography.bodySmall)
        }
    }
}