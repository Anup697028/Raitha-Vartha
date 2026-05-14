package com.raitha.vartha.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TextToSpeechManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("kn", "IN"))
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isReady = true
                }
            }
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
