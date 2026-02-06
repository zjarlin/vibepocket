package site.addzero.vibepocket

import MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.deprecated.openFileSaver
import kotlinx.coroutines.launch

val url = "https://cdn1.suno.ai/b5390e2d-80ba-42c8-820b-b79b0dfc0adb.mp3"

@Composable
fun MediaPlayerDemo() {
    val scope = rememberCoroutineScope ()
    Column {

        MediaPlayer(
            modifier = Modifier.fillMaxWidth(),
            url = url,
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
        Button(
            onClick = {
                // TODO:
                scope.launch {
                    FileKit.openFileSaver(
                        bytes = TODO()
                    )
                }
            },
            content = { Text("download") }
        )

    }


}

