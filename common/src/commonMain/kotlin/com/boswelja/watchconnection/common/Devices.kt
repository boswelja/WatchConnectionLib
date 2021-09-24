package com.boswelja.watchconnection.common

/**
 * The standardised Device representation.
 * @param uid A unique ID assigned to this device.
 * @param name The display name of this device.
 */
public open class Device(
    public open val uid: String,
    public open val name: String
) {

    override fun equals(other: Any?): Boolean {
        if (other !is Device) return super.equals(other)
        return other.uid == uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}

/**
 * The standardised Phone representation.
 * @param uid A unique ID assigned to this phone.
 * @param name The display name of this phone.
 */
public open class Phone(
    uid: String,
    name: String,
) : Device(uid, name)

/**
 * The standardised Watch representation.
 * @param uid A unique ID assigned to this watch.
 * @param name The display name of this watch.
 * @param internalId The ID of this watch assigned by it's platform. You should not need to use
 * this outside of the calling platform.
 * @param platform The platform identifier string of this watch. You should not need to use this
 * outside the calling platform.
 */
public open class Watch(uid: String, name: String,
    public val internalId: String,
    public open val platform: String
) : Device(uid, name) {

    public constructor(
        name: String,
        internalId: String,
        platform: String
    ) : this(
        createUUID(platform, internalId),
        name,
        internalId,
        platform
    )

    internal companion object {

        /**
         * Gets a reproducible, unique ID from the provided info.
         * @param platform The platform identifier for this watches platform.
         * @param platformId See [Watch.internalId].
         */
        fun createUUID(platform: String, platformId: String): String = platform + platformId
    }
}
