package com.mind.play.core.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.mind.play.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SoundManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var uiSoundEnabled = true
    private var gameSoundEnabled = true

    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private var backgroundMusicPlayer: MediaPlayer? = null
    private var isInitialized = false
    private var isMusicStarted = false

    private var soundTap: Int = 0
    private var soundCorrect: Int = 0
    private var soundWrong: Int = 0

    private val simonToneResources = mapOf(
        SimonTone.GREEN to "simon_first",
        SimonTone.ORANGE to "simon_second",
        SimonTone.PINK to "simon_third",
        SimonTone.YELLOW to "simon_forth"
    )

    init {
        initSoundPool()
        observeSettings()
    }

    private fun initSoundPool() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.let { pool ->
                soundTap = loadSoundSafe(pool, "tap")
                soundCorrect = loadSoundSafe(pool, "correct")
                soundWrong = loadSoundSafe(pool, "wrong")
            }

            isInitialized = true
        } catch (e: Exception) {
        }
    }

    private fun loadSoundSafe(pool: SoundPool, name: String): Int {
        return try {
            val resId = context.resources.getIdentifier(name, "raw", context.packageName)
            if (resId != 0) {
                pool.load(context, resId, 1)
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun observeSettings() {
        scope.launch {
            settingsRepository.settings.collectLatest { settings ->
                settings?.let {
                    val wasUiSoundEnabled = uiSoundEnabled
                    uiSoundEnabled = it.uiSoundEnabled
                    gameSoundEnabled = it.gameSoundEnabled

                    if (uiSoundEnabled && !wasUiSoundEnabled) {
                        resumeBackgroundMusic()
                    } else if (!uiSoundEnabled && wasUiSoundEnabled) {
                        pauseBackgroundMusic()
                    }
                }
            }
        }
    }

    fun playTap() {
        if (!uiSoundEnabled) return
        soundPool?.play(soundTap, 1f, 1f, 1, 0, 1f)
    }
    fun playCorrect() {
        if (!gameSoundEnabled) return
        soundPool?.play(soundCorrect, 1f, 1f, 1, 0, 1f)
    }

    fun playWrong() {
        if (!gameSoundEnabled) return
        soundPool?.play(soundWrong, 1f, 1f, 1, 0, 1f)
    }

    fun playSimonTone(tone: SimonTone) {
        if (!gameSoundEnabled) return

        try {
            mediaPlayer?.release()

            val resName = simonToneResources[tone] ?: return
            val resId = context.resources.getIdentifier(resName, "raw", context.packageName)

            if (resId == 0) {
                return
            }

            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
        }
    }

    fun stopSimonTone() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun startBackgroundMusic() {
        if (isMusicStarted || !uiSoundEnabled) return
        
        try {
            val resId = context.resources.getIdentifier("bg_music", "raw", context.packageName)
            if (resId == 0) {
                return
            }
            
            backgroundMusicPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
                start()
            }
            isMusicStarted = true
        } catch (e: Exception) {
        }
    }

    fun pauseBackgroundMusic() {
        backgroundMusicPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun resumeBackgroundMusic() {
        if (!uiSoundEnabled || !isMusicStarted) return
        
        backgroundMusicPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun stopBackgroundMusic() {
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = null
        isMusicStarted = false
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        mediaPlayer?.release()
        mediaPlayer = null
        stopBackgroundMusic()
    }

    enum class SimonTone {
        GREEN, ORANGE, PINK, YELLOW
    }
}

