package com.boswelja.watchconnection.core

class ConcreteBaseClient(
    vararg platforms: Platform
) : BaseClient<Platform>(*platforms) {
    fun getPlatformMap() = platforms
}
