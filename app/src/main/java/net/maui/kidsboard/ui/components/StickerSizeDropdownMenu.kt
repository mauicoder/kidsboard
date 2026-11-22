package net.maui.kidsboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.maui.kidsboard.model.StampSize
import net.maui.kidsboard.ui.components.sticker.StickerRegistry

/**
 * Dropdown submenu that appears on long-pressing a sticker in the tray or dialog,
 * allowing the user to select from preset sticker sizes (Small, Medium, Large, Jumbo).
 */
@Composable
fun StickerSizeDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    emoji: String,
    selectedSize: StampSize,
    onSizeSelected: (StampSize) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(BorderStroke(1.dp, Color(0x1F000000)), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Sticker Size",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StampSize.allSizes.forEach { size ->
                    val isCurrent = size == selectedSize
                    val itemBg = if (isCurrent) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                    val itemBorder = if (isCurrent) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(1.dp, Color(0x18000000))
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(itemBg)
                            .border(itemBorder, RoundedCornerShape(12.dp))
                            .clickable { onSizeSelected(size) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .semantics { contentDescription = "${size.label} size" }
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val customSticker = StickerRegistry.find(emoji)
                            if (customSticker != null) {
                                customSticker.Icon(modifier = Modifier.size(size.previewSizeDp))
                            } else {
                                Text(
                                    text = emoji,
                                    fontSize = (size.previewSizeDp.value * 0.9f).sp
                                )
                            }
                        }

                        Text(
                            text = size.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}
