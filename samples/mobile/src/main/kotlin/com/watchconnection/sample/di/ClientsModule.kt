package com.watchconnection.sample.di

import android.content.Context
import com.boswelja.watchconnection.core.discovery.DiscoveryClient
import com.boswelja.watchconnection.core.message.MessageClient
import com.boswelja.watchconnection.serialization.StringSerializer
import com.boswelja.watchconnection.wearos.discovery.WearOSDiscoveryPlatform
import com.boswelja.watchconnection.wearos.message.WearOSMessagePlatform
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClientsModule {

    @Singleton
    @Provides
    fun discoveryClient(@ApplicationContext applicationContext: Context): DiscoveryClient =
        DiscoveryClient(
            listOf(
                WearOSDiscoveryPlatform(applicationContext)
            )
        )

    @Singleton
    @Provides
    fun messageClient(@ApplicationContext applicationContext: Context): MessageClient =
        MessageClient(
            serializers = listOf(
                StringSerializer(setOf("message-path"))
            ),
            platforms = listOf(
                WearOSMessagePlatform(applicationContext)
            )
        )
}
