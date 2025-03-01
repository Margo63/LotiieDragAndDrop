package com.example.lottiedraganddrop

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.lottiedraganddrop.ui.theme.LottieDragAndDropTheme
import kotlin.math.roundToInt

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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    //lottie композиции
    val compositionHamster by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hamster))
    val compositionHamsterWheel by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hamster_wheel))
    val compositionGoose by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.goose))
    //счетчик попаданий
    var counter by remember { mutableStateOf(0) }


    var screenWidth by remember { mutableStateOf(0) }
    var boxWidth by remember { mutableStateOf(0) }
    // Анимация для смещения Box
    val offsetX = remember { Animatable(0f) }

    val destiny = LocalDensity.current

    LaunchedEffect(Unit) {

        val targetX = with(destiny) { (screenWidth - boxWidth).toFloat() }

        offsetX.animateTo(
            targetX,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    var isRun by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .onSizeChanged { size ->
                screenWidth = size.width
            }

    ) {
        LottieAnimation(
            modifier = Modifier
                .size(200.dp)
                .dragAndDropSource(

                ) {
                    detectTapGestures(
                        onLongPress = {
                            //логика обработки
                            startTransfer(
                                // данные для передачи
                                DragAndDropTransferData(
                                    clipData = ClipData
                                        .newIntent(
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
        Text(
            text = "Очки: "+counter.toString(),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
        )




        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .onSizeChanged { size ->
                    boxWidth = size.width
                }
                .offset { IntOffset(offsetX.value.toInt(), 0) }
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        // проверяет, если Drag and Drop содержит текст intent mime типа
                        event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_INTENT)
                    },
                    target = object : DragAndDropTarget {
                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            isRun = !isRun
                            counter++
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