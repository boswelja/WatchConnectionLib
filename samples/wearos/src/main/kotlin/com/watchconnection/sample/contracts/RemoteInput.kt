package com.watchconnection.sample.contracts

import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.wear.input.RemoteInputIntentHelper

class RemoteInput : ActivityResultContract<String, String?>() {

    private val resultKey = "input-result"

    override fun createIntent(context: Context, input: String): Intent {
        val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
        val intentWithTitle = RemoteInputIntentHelper.putTitleExtra(intent, input)
        return RemoteInputIntentHelper.putRemoteInputsExtra(
            intentWithTitle,
            listOf(
                RemoteInput.Builder(resultKey)
                    .setAllowFreeFormInput(true)
                    .build()
            )
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return intent?.let {
            RemoteInput.getResultsFromIntent(it).getCharSequence(resultKey)?.toString()
        }
    }
}
