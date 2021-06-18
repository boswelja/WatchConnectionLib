package com.boswelja.watchconnection.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.get
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEmpty

@ExperimentalCoroutinesApi
class BaseClientTest {

    @Test
    fun `BaseClient throws exception when no platforms are provided`() {
        expectThrows<IllegalArgumentException> { ConcreteBaseClient() }
    }

    @Test
    fun `BaseClient doesn't throw exception when platforms are provided`() {
        ConcreteBaseClient(ConcretePlatform())
    }

    @Test
    fun `BaseClient maps platforms to identifiers`() {
        val platform = ConcretePlatform()
        val client = ConcreteBaseClient(platform)
        expectThat(client.getPlatformMap()) {
            isNotEmpty()
            get(platform.platformIdentifier).isEqualTo(platform)
        }
    }
}
