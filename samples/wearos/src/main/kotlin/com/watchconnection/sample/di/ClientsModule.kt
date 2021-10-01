package com.watchconnection.sample.di

import android.content.Context
import com.boswelja.watchconnection.serializers.StringSerializer
import com.boswelja.watchconnection.wear.discovery.DiscoveryClient
import com.boswelja.watchconnection.wear.message.MessageClient
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
        DiscoveryClient(applicationContext)

    @Singleton
    @Provides
    fun messageClient(@ApplicationContext applicationContext: Context): MessageClient =
        MessageClient(
            applicationContext,
            listOf(StringSerializer(setOf("message-path")))
        )
}
