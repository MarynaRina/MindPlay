package com.mind.play

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mind.play.core.navigation.MindPlayNavigation
import com.mind.play.core.sound.SoundManager
import com.mind.play.ui.theme.ProvideTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val soundManager: SoundManager by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                soundManager.resumeBackgroundMusic()
            }
        }
        
        setContent {
            ProvideTheme {
                MindPlayNavigation()
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        soundManager.pauseBackgroundMusic()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}
