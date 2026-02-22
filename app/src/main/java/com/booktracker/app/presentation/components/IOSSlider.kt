package com.booktracker.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.booktracker.app.presentation.theme.IOSTheme

@Composable
fun IOSSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackColor: Color = IOSTheme.colors.progressTrack,
    fillColor: Color = IOSTheme.colors.progressFill
) {
    val density = LocalDensity.current
    var trackWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .onGloballyPositioned { trackWidth = it.size.width }
            .pointerInput(trackWidth) {
                if (trackWidth <= 0) return@pointerInput
                detectTapGestures { offset ->
                    val newValue = (offset.x / trackWidth).coerceIn(0f, 1f)
                    onValueChange(newValue)
                }
            }
            .pointerInput(trackWidth) {
                if (trackWidth <= 0) return@pointerInput
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    val currentPx = value * trackWidth
                    val newPx = (currentPx + dragAmount).coerceIn(0f, trackWidth.toFloat())
                    onValueChange(newPx / trackWidth)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Track background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(IOSTheme.shapes.progressBar)
                .background(trackColor)
        )

        // Filled track
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = value.coerceIn(0f, 1f))
                .height(3.dp)
                .clip(IOSTheme.shapes.progressBar)
                .background(fillColor)
        )

        // Thumb
        if (trackWidth > 0) {
            val thumbSize = 28.dp
            val halfThumbPx = with(density) { (thumbSize / 2).toPx() }
            val offsetPx = (value.coerceIn(0f, 1f) * trackWidth) - halfThumbPx
            val offsetDp = with(density) { offsetPx.coerceAtLeast(0f).toDp() }

            Box(
                modifier = Modifier
                    .offset(x = offsetDp)
                    .size(thumbSize)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.15f),
                        spotColor = Color.Black.copy(alpha = 0.15f)
                    )
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}
