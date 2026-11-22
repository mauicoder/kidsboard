package net.maui.kidsboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.PaletteColor

@Composable
fun ColorPickerDialog(
    selectedColor: Color,
    onColorSelected: (PaletteColor) -> Unit,
    onDismiss: () -> Unit,
    isRainbow: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Choose a Color",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Colors arranged in rows of 4 columns
                ColorPalette.allColors.chunked(4).forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowColors.forEach { paletteColor ->
                            val isSelected = if (paletteColor.isRainbow) {
                                isRainbow
                            } else {
                                !isRainbow && paletteColor.color == selectedColor
                            }
                            ColorSwatch(
                                paletteColor = paletteColor,
                                isSelected = isSelected,
                                onClick = { onColorSelected(paletteColor) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ColorSwatch(
    paletteColor: PaletteColor,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = paletteColor.color.luminance() > 0.5f
    val checkColor = if (isLight) Color.Black else Color.White

    val rainbowBrush = Brush.sweepGradient(ColorPalette.RainbowSpectrum)

    val backgroundModifier = if (paletteColor.isRainbow) {
        Modifier.background(rainbowBrush)
    } else {
        Modifier.background(paletteColor.color)
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .then(backgroundModifier)
            .border(
                BorderStroke(2.dp, Color(0x33000000)),
                CircleShape
            )
            .clickable(onClick = onClick)
            .semantics { contentDescription = paletteColor.name },
        contentAlignment = Alignment.Center
    ) {
        if (paletteColor.isRainbow) {
            Rainbow180StickerIcon(modifier = Modifier.size(28.dp))
        } else if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = checkColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun Color.luminance(): Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}
