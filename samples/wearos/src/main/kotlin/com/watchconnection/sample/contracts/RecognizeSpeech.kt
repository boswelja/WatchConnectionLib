package com.watchconnection.sample.contracts

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.contract.ActivityResultContract

class RecognizeSpeech : ActivityResultContract<String, String?>() {
    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, input)
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
    }
}
