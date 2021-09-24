package com.boswelja.watchconnection.core

class ConcreteBaseClient(
    platforms: List<Platform>
) : BaseClient<Platform>(platforms) {
    fun getPlatformMap() = platforms
}
