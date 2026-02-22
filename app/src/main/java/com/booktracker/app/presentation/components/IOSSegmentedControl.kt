package com.booktracker.app.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.booktracker.app.presentation.theme.IOSTheme

@Composable
fun IOSSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = IOSTheme.colors
    val typography = IOSTheme.typography
    val shapes = IOSTheme.shapes
    val density = LocalDensity.current

    val segmentedControlShape = shapes.segmentedControl
    val itemShape = shapes.segmentedControlItem

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IOSTheme.dimensions.segmentedControlHeight)
            .clip(segmentedControlShape)
            .background(colors.segmentedControlBackground)
            .onGloballyPositioned { containerSize = it.size }
    ) {
        if (items.isNotEmpty() && containerSize.width > 0) {
            val itemWidth = with(density) { (containerSize.width / items.size).toDp() }
            val offsetX by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = tween(200),
                label = "segmentOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .padding(2.dp)
                    .width(itemWidth - 4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 0.dp)
                    .shadow(
                        elevation = 1.dp,
                        shape = itemShape,
                        ambientColor = Color.Black.copy(alpha = 0.08f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    )
                    .clip(itemShape)
                    .background(colors.segmentedControlSelected)
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = label,
                        style = typography.footnote.copy(
                            fontWeight = if (index == selectedIndex) FontWeight.SemiBold else FontWeight.Normal,
                            color = colors.label
                        )
                    )
                }
            }
        }
    }
}
