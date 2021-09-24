package com.watchconnection.sample

import android.content.Context
import com.boswelja.watchconnection.serializers.StringSerializer
import com.boswelja.watchconnection.wear.discovery.DiscoveryClient
import com.boswelja.watchconnection.wear.message.MessageClient

fun Context.discoveryClient(): DiscoveryClient = DiscoveryClient(this)

fun Context.messageClient(): MessageClient =
    MessageClient(
        this,
        listOf(StringSerializer(setOf("message-path")))
    )
