package net.maui.kidsboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.PaletteColor
import net.maui.kidsboard.model.StampRepository

val WarmDeskBackground = Color(0xFFF9F6F0)
val WarmDeskSurface = Color(0xFFF2ECE1)
val WarmDeskBorder = Color(0x18000000)

val QuickStampsList = listOf("⭐", "❤️", StampRepository.FULL_RAINBOW, "🌈", "✨", "🎈", "🐶", "🐱", "🚀", "🍦", "🌸")

@Composable
fun QuickColorSwatch(
    paletteColor: PaletteColor,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = paletteColor.color.luminance() > 0.5f
    val checkColor = if (isLight) Color.Black else Color.White
    val borderStroke = if (isSelected) {
        BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.5.dp, Color(0x22000000))
    }

    val rainbowBrush = remember {
        Brush.sweepGradient(ColorPalette.RainbowSpectrum)
    }

    val backgroundModifier = if (paletteColor.isRainbow) {
        Modifier.background(rainbowBrush)
    } else {
        Modifier.background(paletteColor.color)
    }

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .then(backgroundModifier)
            .border(borderStroke, CircleShape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = paletteColor.name },
        contentAlignment = Alignment.Center
    ) {
        if (paletteColor.isRainbow) {
            Text(
                text = "🌈",
                fontSize = 18.sp
            )
        } else if (paletteColor.isEraser) {
            Icon(
                imageVector = Icons.Default.AutoFixHigh,
                contentDescription = "Eraser",
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF757575),
                modifier = Modifier.size(20.dp)
            )
        } else if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = checkColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun QuickColorRow(
    selectedColor: Color,
    isEraser: Boolean,
    isRainbow: Boolean,
    onColorSelected: (PaletteColor) -> Unit,
    onOpenFullPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ColorPalette.allColors.forEach { paletteColor ->
            val isSelected = if (paletteColor.isEraser) {
                isEraser
            } else if (paletteColor.isRainbow) {
                isRainbow
            } else {
                !isEraser && !isRainbow && paletteColor.color == selectedColor
            }
            QuickColorSwatch(
                paletteColor = paletteColor,
                isSelected = isSelected,
                onClick = { onColorSelected(paletteColor) }
            )
        }

        // Full Color Picker Button
        IconButton(
            onClick = onOpenFullPicker,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(BorderStroke(1.dp, Color(0x22000000)), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "More Colors",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun QuickColorGrid(
    selectedColor: Color,
    isEraser: Boolean,
    isRainbow: Boolean,
    onColorSelected: (PaletteColor) -> Unit,
    onOpenFullPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val colors = ColorPalette.allColors
        colors.chunked(2).forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pair.forEach { paletteColor ->
                    val isSelected = if (paletteColor.isEraser) {
                        isEraser
                    } else if (paletteColor.isRainbow) {
                        isRainbow
                    } else {
                        !isEraser && !isRainbow && paletteColor.color == selectedColor
                    }
                    QuickColorSwatch(
                        paletteColor = paletteColor,
                        isSelected = isSelected,
                        onClick = { onColorSelected(paletteColor) }
                    )
                }
            }
        }

        // More Colors Button
        IconButton(
            onClick = onOpenFullPicker,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(BorderStroke(1.dp, Color(0x22000000)), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "More Colors",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun QuickStickerChip(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderStroke = if (isSelected) {
        BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, Color(0x22000000))
    }

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(borderStroke, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = if (emoji == StampRepository.FULL_RAINBOW) "Full Rainbow stamp" else "$emoji stamp"
            },
        contentAlignment = Alignment.Center
    ) {
        if (emoji == StampRepository.FULL_RAINBOW) {
            FullRainbowStickerIcon(modifier = Modifier.size(28.dp))
        } else {
            Text(
                text = emoji,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun QuickStickerRow(
    selectedStamp: String,
    isStampActive: Boolean,
    onStampSelected: (String) -> Unit,
    onOpenFullPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuickStampsList.forEach { emoji ->
            val isSelected = isStampActive && emoji == selectedStamp
            QuickStickerChip(
                emoji = emoji,
                isSelected = isSelected,
                onClick = { onStampSelected(emoji) }
            )
        }

        // More Stickers Button
        IconButton(
            onClick = onOpenFullPicker,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(BorderStroke(1.dp, Color(0x22000000)), RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "More Stickers",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun QuickStickerGrid(
    selectedStamp: String,
    isStampActive: Boolean,
    onStampSelected: (String) -> Unit,
    onOpenFullPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuickStampsList.chunked(2).forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pair.forEach { emoji ->
                    val isSelected = isStampActive && emoji == selectedStamp
                    QuickStickerChip(
                        emoji = emoji,
                        isSelected = isSelected,
                        onClick = { onStampSelected(emoji) }
                    )
                }
            }
        }

        // More Stickers Button
        IconButton(
            onClick = onOpenFullPicker,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(BorderStroke(1.dp, Color(0x22000000)), RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "More Stickers",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun ChunkyActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = if (enabled) 0.95f else 0.5f))
            .border(BorderStroke(1.dp, Color(0x1A000000)), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else tint.copy(alpha = 0.38f),
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun Color.luminance(): Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}
