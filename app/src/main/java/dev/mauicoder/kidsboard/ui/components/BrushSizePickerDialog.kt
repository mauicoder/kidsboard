package dev.mauicoder.kidsboard.ui.components

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import dev.mauicoder.kidsboard.model.BrushSize
import dev.mauicoder.kidsboard.model.ColorPalette

@Composable
fun BrushSizePickerDialog(
    selectedBrushSize: BrushSize,
    activeColor: Color,
    isRainbow: Boolean = false,
    onBrushSizeSelected: (BrushSize) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Brush Thickness",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrushSize.allSizes.forEach { preset ->
                    val isSelected = preset == selectedBrushSize
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onBrushSizeSelected(preset) }
                            .padding(8.dp)
                            .semantics { contentDescription = "${preset.label} brush" }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            border = if (isSelected) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                null
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val rainbowBrush = Brush.sweepGradient(ColorPalette.RainbowSpectrum)
                                Box(
                                    modifier = Modifier
                                        .size(preset.previewSizeDp)
                                        .clip(CircleShape)
                                        .then(
                                            if (isRainbow && activeColor != Color.White) {
                                                Modifier.background(rainbowBrush)
                                            } else {
                                                Modifier.background(activeColor)
                                            }
                                        )
                                        .border(
                                            BorderStroke(1.dp, Color(0x33000000)),
                                            CircleShape
                                        )
                                )
                            }
                        }

                        Text(
                            text = preset.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
