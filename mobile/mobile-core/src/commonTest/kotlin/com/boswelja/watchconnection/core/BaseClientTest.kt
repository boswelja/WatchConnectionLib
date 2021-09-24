package com.boswelja.watchconnection.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BaseClientTest {

    @Test
    fun baseClientThrowsExceptionWhenNoPlatformsAreProvided() {
        assertFailsWith<IllegalArgumentException> {
            ConcreteBaseClient(listOf())
        }
    }

    @Test
    fun baseClientDoesNotThrowExceptionWhenPlatformsAreProvided() {
        ConcreteBaseClient(listOf(ConcretePlatform()))
    }

    @Test
    fun baseClientMapsPlatformsToIdentifiers() {
        val platform = ConcretePlatform()
        val client = ConcreteBaseClient(listOf(platform))

        val platformMap = client.getPlatformMap()
        assertEquals(platform, platformMap[platform.platformIdentifier])
    }
}
