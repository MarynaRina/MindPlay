package com.mind.play.core.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.mind.play.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Менеджер звуків для MindPlay
 *
 * Підтримує:
 * - Короткі звуки (SoundPool): tap, correct, wrong
 * - Тони Simon (MediaPlayer): simon_green, simon_orange, simon_pink, simon_yellow
 *
 * Читає налаштування з SettingsRepository:
 * - uiSoundEnabled - звуки інтерфейсу (tap)
 * - gameSoundEnabled - звуки ігор (correct, wrong, simon tones)
 */
class SoundManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    companion object {
        private const val TAG = "SoundManager"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var uiSoundEnabled = true
    private var gameSoundEnabled = true

    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isInitialized = false

    // Sound IDs for SoundPool
    private var soundTap: Int = 0
    private var soundCorrect: Int = 0
    private var soundWrong: Int = 0

    // Simon tone resource IDs (will be loaded dynamically)
    private val simonToneResources = mapOf(
        SimonTone.GREEN to "simon_green",
        SimonTone.ORANGE to "simon_orange",
        SimonTone.PINK to "simon_pink",
        SimonTone.YELLOW to "simon_yellow"
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
            Log.e(TAG, "Failed to initialize SoundPool", e)
        }
    }

    private fun loadSoundSafe(pool: SoundPool, name: String): Int {
        return try {
            val resId = context.resources.getIdentifier(name, "raw", context.packageName)
            if (resId != 0) {
                pool.load(context, resId, 1)
            } else {
                Log.w(TAG, "Sound resource not found: $name")
                0
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load sound: $name", e)
            0
        }
    }

    private fun observeSettings() {
        scope.launch {
            settingsRepository.settings.collectLatest { settings ->
                settings?.let {
                    uiSoundEnabled = it.uiSoundEnabled
                    gameSoundEnabled = it.gameSoundEnabled
                }
            }
        }
    }

    /**
     * Відтворює звук натискання (UI)
     */
    fun playTap() {
        if (!uiSoundEnabled) return
        soundPool?.play(soundTap, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Відтворює звук правильної відповіді (Game)
     */
    fun playCorrect() {
        if (!gameSoundEnabled) return
        soundPool?.play(soundCorrect, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Відтворює звук неправильної відповіді (Game)
     */
    fun playWrong() {
        if (!gameSoundEnabled) return
        soundPool?.play(soundWrong, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Відтворює тон Simon (Game)
     * @param tone - колір тону (GREEN, ORANGE, PINK, YELLOW)
     */
    fun playSimonTone(tone: SimonTone) {
        if (!gameSoundEnabled) return

        try {
            // Зупинити попередній тон
            mediaPlayer?.release()

            val resName = simonToneResources[tone] ?: return
            val resId = context.resources.getIdentifier(resName, "raw", context.packageName)

            if (resId == 0) {
                Log.w(TAG, "Simon tone not found: $resName")
                return
            }

            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to play Simon tone: ${tone.name}", e)
        }
    }

    /**
     * Зупинити поточний Simon тон
     */
    fun stopSimonTone() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Звільнити ресурси
     */
    fun release() {
        soundPool?.release()
        soundPool = null
        mediaPlayer?.release()
        mediaPlayer = null
    }

    enum class SimonTone {
        GREEN, ORANGE, PINK, YELLOW
    }
}

