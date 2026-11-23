package dev.mauicoder.kidsboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mauicoder.kidsboard.model.StampCategory
import dev.mauicoder.kidsboard.model.StampRepository
import dev.mauicoder.kidsboard.model.StampSize
import dev.mauicoder.kidsboard.ui.components.sticker.StickerRegistry

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun StampPickerDialog(
    selectedStamp: String,
    selectedStampSize: StampSize = StampSize.DEFAULT,
    onStampSelected: (String) -> Unit,
    onStampSizeSelected: (StampSize) -> Unit = {},
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    categories: List<StampCategory> = StampRepository.categories
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Pick a Stamp 🎨",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sticker size selector bar
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Sticker Size",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StampSize.allSizes.forEach { size ->
                            val isSizeSelected = size == selectedStampSize
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSizeSelected) MaterialTheme.colorScheme.primaryContainer
                                        else Color.Transparent
                                    )
                                    .border(
                                        if (isSizeSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                        else BorderStroke(0.dp, Color.Transparent),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onStampSizeSelected(size) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = size.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSizeSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSizeSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                categories.forEach { category ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            category.stamps.forEach { stamp ->
                                val isSelected = stamp == selectedStamp
                                val backgroundColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                                val borderStroke = if (isSelected) {
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                } else {
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                }

                                var showSizeMenu by remember { mutableStateOf(false) }

                                Box {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(backgroundColor)
                                            .border(borderStroke, RoundedCornerShape(16.dp))
                                            .combinedClickable(
                                                onClick = { onStampSelected(stamp) },
                                                onLongClick = {
                                                    onStampSelected(stamp)
                                                    showSizeMenu = true
                                                },
                                                onLongClickLabel = "Change sticker size"
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val customSticker = StickerRegistry.find(stamp)
                                        if (customSticker != null) {
                                            customSticker.Icon(modifier = Modifier.size(36.dp))
                                        } else {
                                            Text(
                                                text = stamp,
                                                fontSize = 28.sp
                                            )
                                        }

                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(2.dp)
                                                    .size(14.dp)
                                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = selectedStampSize.shortLabel,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    }

                                    StickerSizeDropdownMenu(
                                        expanded = showSizeMenu,
                                        onDismiss = { showSizeMenu = false },
                                        emoji = stamp,
                                        selectedSize = selectedStampSize,
                                        onSizeSelected = { size ->
                                            onStampSelected(stamp)
                                            onStampSizeSelected(size)
                                            showSizeMenu = false
                                        }
                                    )
                                }
                            }
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
