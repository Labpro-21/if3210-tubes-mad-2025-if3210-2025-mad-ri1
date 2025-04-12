package com.example.pertamaxify.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pertamaxify.R
import com.example.pertamaxify.ui.theme.RedBackground
import com.example.pertamaxify.ui.theme.WhiteHint
import com.example.pertamaxify.ui.theme.WhiteText


@Composable
fun MiniPlayer(){
    var isPlaying by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    val progress = 0.4f
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = RedBackground.copy(alpha = 0.56f)
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(6.dp)) {
            Row (Modifier.align(Alignment.CenterStart)) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(50.dp)
                )
                // Song title and artist
                Column(Modifier.padding(horizontal = 4.dp)) {
                    Text(
                        text = "Starboy",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = WhiteText
                    )
                    Text(
                        text = "The Weeknd",
                        style = MaterialTheme.typography.bodyMedium ,
                        color = Color.Gray
                    )
                }
            }


            Row (Modifier.align(Alignment.CenterEnd)) {
                IconButton(
                    onClick = { isLiked = !isLiked },
                ) {
                    if (isLiked) {
                        Icon(
                            painter = painterResource(R.drawable.tabler_heart_filled),
                            contentDescription = "Liked",
                            tint = WhiteText,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    else {
                        Icon(
                            painter = painterResource(R.drawable.tabler_heart),
                            contentDescription = "Not Liked",
                            tint = WhiteText,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                ) {
                    if (isPlaying) {
                        Icon(
                            painter = painterResource(R.drawable.pause),
                            contentDescription = "Pause",
                            tint = WhiteText,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = WhiteText,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            color = WhiteText,
            trackColor = WhiteHint,
            gapSize = 0.dp,
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .fillMaxWidth()
                .height(3.dp).align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
fun MiniPlayerPreview() {
    MiniPlayer()
}