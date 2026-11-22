package net.maui.kidsboard

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        binding.btnColorPicker.setBackgroundColor(paintModel.paint.color)
        binding.btnColorPicker.setOnClickListener {
            openColorPicker()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun openColorPicker() {
        val dialog = Dialog(this, R.style.ColorDialog)
        val dialogBinding = ColorPickBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        val colorButtons = listOf(
            dialogBinding.btnColorBlack to R.color.black,
            dialogBinding.btnColorBlue to R.color.blue,
            dialogBinding.btnColorGreen to R.color.green,
            dialogBinding.btnColorRed to R.color.red,
            dialogBinding.btnColorDarkBlue to R.color.dark_blue,
            dialogBinding.btnColorMagenta to R.color.magenta,
            dialogBinding.btnColorOrange to R.color.orange,
            dialogBinding.btnColorYellow to R.color.yellow,
            dialogBinding.btnColorWhite to R.color.white,
            dialogBinding.btnColorCyan to R.color.cyan,
            dialogBinding.btnColorBrown to R.color.brown_bear,
            dialogBinding.btnColorCoffee to R.color.coffee
        )

        for ((button, colorResId) in colorButtons) {
            setupColorButton(button, colorResId, dialog)
        }

        dialog.show()
    }

    private fun setupColorButton(button: Button, colorResId: Int, dialog: Dialog) {
        val color = ContextCompat.getColor(this, colorResId)
        button.setOnClickListener {
            paintModel.setColor(color)
            binding.btnColorPicker.setBackgroundColor(color)
            dialog.dismiss()
        }
    }
}
