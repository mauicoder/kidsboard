package dev.mauicoder.kidsboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.mauicoder.kidsboard.ui.KidsBoardScreen
import dev.mauicoder.kidsboard.ui.theme.KidsBoardTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KidsBoardTheme {
                KidsBoardScreen()
            }
        }
    }
}
