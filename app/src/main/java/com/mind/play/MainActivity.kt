package com.mind.play

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mind.play.core.navigation.MindPlayNavigation
import com.mind.play.ui.theme.ProvideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProvideTheme {
                MindPlayNavigation()
            }
        }
    }
}
