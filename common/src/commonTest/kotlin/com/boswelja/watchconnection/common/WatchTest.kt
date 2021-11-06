package com.boswelja.watchconnection.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class WatchTest {

    @Test
    fun createUid_failsWithEmptyPlatform() {
        assertFails {
            Watch.createUid("", "platformId")
        }
    }

    @Test
    fun createUid_failsWithEmptyPlatformId() {
        assertFails {
            Watch.createUid("platform", "")
        }
    }

    @Test
    fun createUid_succeedsWithValidData() {
        Watch.createUid("platform", "platformId")
    }

    @Test
    fun getInfoFromUid_failsWithInvalidUid() {
        assertFails {
            Watch.getInfoFromUid("invalidUid")
        }
    }

    @Test
    fun getInfoFromUid_succeedsWithValidUid() {
        // Create data
        val platform = "platform"
        val platformId = "platformId"
        val validUid = Watch.createUid(platform, platformId)

        // Make the call and check result
        val info = Watch.getInfoFromUid(validUid)
        assertEquals(platform, info.first)
        assertEquals(platformId, info.second)
    }
}
