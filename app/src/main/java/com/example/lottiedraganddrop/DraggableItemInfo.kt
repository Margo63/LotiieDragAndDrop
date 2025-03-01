package com.example.lottiedraganddrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

@Composable
fun <T> DraggableView(

){
    var currentPosition by remember{
        mutableStateOf(Offset.Zero)
    }
}

internal class DraggableItemInfo {
    var isDragging by mutableStateOf(false)
    var dragStartOffset by mutableStateOf(Offset.Zero)
    var dragCurrentOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable ()->Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
}