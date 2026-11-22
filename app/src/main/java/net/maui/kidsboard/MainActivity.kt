package net.maui.kidsboard

import android.app.Dialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import net.maui.kidsboard.databinding.ActivityMainBinding
import net.maui.kidsboard.databinding.ColorPickBinding
import net.maui.kidsboard.model.PaintModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val paintModel: PaintModel = PaintModel.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateColorPickerButton(paintModel.paint.color)
        binding.btnColorPicker.setOnClickListener {
            openColorPicker()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun updateColorPickerButton(color: Int) {
        val density = resources.displayMetrics.density
        val strokeWidth = (2 * density).toInt()
        val shape = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setStroke(
                strokeWidth,
                ContextCompat.getColor(this@MainActivity, R.color.color_swatch_stroke)
            )
        }
        binding.btnColorPicker.background = shape
    }

    private fun openColorPicker() {
        val dialog = Dialog(this, R.style.ColorDialog)
        val dialogBinding = ColorPickBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        val colors = listOf(
            ColorOption(ContextCompat.getColor(this, R.color.black), getString(R.string.color_black)),
            ColorOption(ContextCompat.getColor(this, R.color.dark_blue), getString(R.string.color_dark_blue)),
            ColorOption(ContextCompat.getColor(this, R.color.blue), getString(R.string.color_blue)),
            ColorOption(ContextCompat.getColor(this, R.color.cyan), getString(R.string.color_cyan)),
            ColorOption(ContextCompat.getColor(this, R.color.green), getString(R.string.color_green)),
            ColorOption(ContextCompat.getColor(this, R.color.yellow), getString(R.string.color_yellow)),
            ColorOption(ContextCompat.getColor(this, R.color.orange), getString(R.string.color_orange)),
            ColorOption(ContextCompat.getColor(this, R.color.red), getString(R.string.color_red)),
            ColorOption(ContextCompat.getColor(this, R.color.magenta), getString(R.string.color_magenta)),
            ColorOption(ContextCompat.getColor(this, R.color.brown_bear), getString(R.string.color_brown)),
            ColorOption(ContextCompat.getColor(this, R.color.coffee), getString(R.string.color_coffee)),
            ColorOption(ContextCompat.getColor(this, R.color.white), getString(R.string.color_white_eraser), isEraser = true)
        )

        val adapter = ColorPaletteAdapter(colors, paintModel.paint.color) { selectedColor ->
            paintModel.setColor(selectedColor)
            updateColorPickerButton(selectedColor)
            dialog.dismiss()
        }

        dialogBinding.rvColors.layoutManager = GridLayoutManager(this, 4)
        dialogBinding.rvColors.adapter = adapter

        dialog.show()
    }
}
