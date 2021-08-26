package com.boswelja.watchconnection.core

import org.junit.Assert
import org.junit.Test

class BaseClientTest {

    @Test
    fun `BaseClient throws exception when no platforms are provided`() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            ConcreteBaseClient(listOf())
        }
    }

    @Test
    fun `BaseClient doesn't throw exception when platforms are provided`() {
        ConcreteBaseClient(listOf(ConcretePlatform()))
    }

    @Test
    fun `BaseClient maps platforms to identifiers`() {
        val platform = ConcretePlatform()
        val client = ConcreteBaseClient(listOf(platform))

        val platformMap = client.getPlatformMap()
        Assert.assertEquals(platform, platformMap[platform.platformIdentifier])
    }
}
