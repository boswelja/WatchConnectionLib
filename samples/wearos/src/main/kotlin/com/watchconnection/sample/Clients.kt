package com.watchconnection.sample

import android.content.Context
import com.boswelja.watchconnection.common.message.serialized.StringSerializer
import com.boswelja.watchconnection.wearos.discovery.DiscoveryClient
import com.boswelja.watchconnection.wearos.message.MessageClient

fun Context.discoveryClient(): DiscoveryClient =
    DiscoveryClient(
        this,
        "watchconnection-sample",
        Capabilities.values().map { it.name }
    )

fun Context.messageClient(): MessageClient =
    MessageClient(
        this,
        listOf(StringSerializer(setOf("message-path")))
    )
