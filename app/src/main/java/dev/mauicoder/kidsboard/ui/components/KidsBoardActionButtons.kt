package dev.mauicoder.kidsboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.mauicoder.kidsboard.model.BrushSize
import dev.mauicoder.kidsboard.model.ColorPalette

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

@Composable
fun BrushSizeActionButton(
    brushSize: BrushSize,
    selectedColor: Color,
    isEraser: Boolean,
    isRainbow: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayColor = if (isEraser) Color.White else selectedColor
    val rainbowBrush = Brush.sweepGradient(ColorPalette.RainbowSpectrum)
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .border(BorderStroke(1.dp, Color(0x1A000000)), CircleShape)
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Brush,
                contentDescription = "Brush & Stamp Size",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
            // Active brush size dot indicator
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(10.dp)
                    .clip(CircleShape)
                    .then(
                        if (isRainbow && !isEraser) {
                            Modifier.background(rainbowBrush)
                        } else {
                            Modifier.background(displayColor)
                        }
                    )
                    .border(BorderStroke(1.dp, Color(0x33000000)), CircleShape)
            )
        }
    }
}

@Composable
fun SaveActionButton(
    isSaving: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled && !isSaving,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                if (enabled) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
            .border(
                BorderStroke(
                    1.dp,
                    if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color(0x1A000000)
                ),
                CircleShape
            )
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Save and Share Drawing",
                tint = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun SoundActionButton(
    isSoundEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChunkyActionButton(
        icon = if (isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
        contentDescription = if (isSoundEnabled) "Mute Sound" else "Unmute Sound",
        onClick = onClick,
        modifier = modifier,
        tint = if (isSoundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    )
}

@Composable
fun CanvasStyleActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChunkyActionButton(
        icon = Icons.Default.AutoStories,
        contentDescription = "Canvas & Coloring Pages",
        onClick = onClick,
        modifier = modifier,
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun MagicWandActionButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBackgroundColor = Color(0xFFFFE082)
    val inactiveBackgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    val activeBorderColor = Color(0xFFFFB300)
    val inactiveBorderColor = Color(0x1A000000)
    val activeTint = Color(0xFFE65100)
    val inactiveTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (isActive) activeBackgroundColor else inactiveBackgroundColor)
            .border(
                BorderStroke(
                    if (isActive) 1.5.dp else 1.dp,
                    if (isActive) activeBorderColor else inactiveBorderColor
                ),
                CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Default.AutoFixHigh,
            contentDescription = if (isActive) "Magic Stay Inside Lines (Enabled)" else "Magic Stay Inside Lines (Disabled)",
            tint = if (isActive) activeTint else inactiveTint,
            modifier = Modifier.size(24.dp)
        )
    }
}
