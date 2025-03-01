package com.example.lottiedraganddrop

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.lottiedraganddrop.ui.theme.LottieDragAndDropTheme
//https://habr.com/ru/articles/800389/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LottieDragAndDropTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

private const val transferAction = "action"
private const val transferData = "data"

private fun ClipData.getRun(): Boolean? {
    return (0 until itemCount)
        .mapNotNull(::getItemAt).firstNotNullOfOrNull { item ->
            item.intent?.getStringExtra(transferData)?.takeIf { it.isNotEmpty() }
        }?.let { !it.toBoolean() }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val compositionHamster by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hamster))
    val compositionHamsterWheel by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hamster_wheel))
    val compositionGoose by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.goose))

    var isRun by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(200.dp)
                .dragAndDropSource(

                ) {
                    detectTapGestures(
                        onLongPress = {
                            startTransfer(
                                DragAndDropTransferData(
                                    clipData = ClipData.newIntent(
                                        "isRun",
                                        Intent(transferAction).apply {
                                            putExtra(
                                                transferData,
                                                isRun
                                            )
                                        },
                                    )
                                )
                            )
                        }
                    )
                },
            composition = compositionGoose,
            iterations = LottieConstants.IterateForever
        )

        Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .dragAndDropTarget(
                        shouldStartDragAndDrop = { event ->
                            // проверяет, если Drag and Drop содержит текст intent mime типа
                            event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_INTENT)
                        },
                        target = object : DragAndDropTarget {
                            override fun onDrop(event: DragAndDropEvent): Boolean {
                                isRun = !isRun
                                return true
                            }
                        }
                    )
        ) {
            if (isRun)
                LottieAnimation(
                    modifier = Modifier
                        .size(200.dp),
                    composition = compositionHamsterWheel,
                    iterations = LottieConstants.IterateForever
                )
            else
                LottieAnimation(
                    modifier = Modifier
                        .size(200.dp),
                    composition = compositionHamster,
                    iterations = LottieConstants.IterateForever
                )
        }

    }


}