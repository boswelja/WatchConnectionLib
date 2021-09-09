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
