package site.addzero.vibepocket

import MediaPlayer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val string = "https://cdn1.suno.ai/b5390e2d-80ba-42c8-820b-b79b0dfc0adb.mp3"
@Composable
fun MediaPlayerDemo() {

    MediaPlayer(
        modifier = Modifier.fillMaxWidth(),
        url = string,
        startTime = Color.Black,
        endTime = Color.Black,
        volumeIconColor = Color.Black,
        playIconColor = Color.Blue,
        sliderTrackColor = Color.LightGray,
        sliderIndicatorColor = Color.Blue,
        showControls = true,
        headers = mapOf(),
        autoPlay = true,
    )


}

