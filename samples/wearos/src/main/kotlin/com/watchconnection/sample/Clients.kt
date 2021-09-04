package com.watchconnection.sample

import android.content.Context
import com.boswelja.watchconnection.wearos.discovery.DiscoveryClient

fun Context.discoveryClient(): DiscoveryClient =
    DiscoveryClient(
        this,
        "watchconnection-sample",
        Capabilities.values().map { it.name }
    )
