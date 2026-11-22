package net.maui.kidsboard

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.maui.kidsboard.databinding.ColorSwatchItemBinding

data class ColorOption(
    val color: Int,
    val name: String,
    val isEraser: Boolean = false
)

object ColorPaletteHelper {
    /**
     * Determines whether the color is perceived as light according to the standard
     * ITU-R BT.601 relative luminance formula.
     */
    fun isLightColor(color: Int): Boolean {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
        return luminance > 0.5
    }

    /**
     * Returns contrasting checkmark color (black for light backgrounds, white for dark backgrounds).
     */
    fun getContrastingColor(color: Int): Int {
        return if (isLightColor(color)) Color.BLACK else Color.WHITE
    }
}

class ColorPaletteAdapter(
    private val colors: List<ColorOption>,
    private val selectedColor: Int,
    private val onColorSelected: (Int) -> Unit
) : RecyclerView.Adapter<ColorPaletteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ColorSwatchItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ColorSwatchItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = colors[position]
        val button = holder.binding.btnSwatch

        // Apply swatch background color
        button.backgroundTintList = ColorStateList.valueOf(option.color)
        button.contentDescription = option.name

        // Show checkmark on the currently selected color
        val isSelected = option.color == selectedColor
        if (isSelected) {
            button.setIconResource(R.drawable.ic_check)
            val iconColor = ColorPaletteHelper.getContrastingColor(option.color)
            button.iconTint = ColorStateList.valueOf(iconColor)
        } else {
            button.icon = null
        }

        button.setOnClickListener {
            onColorSelected(option.color)
        }
    }

    override fun getItemCount(): Int = colors.size
}
