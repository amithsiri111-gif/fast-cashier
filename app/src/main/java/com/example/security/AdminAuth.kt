package com.example.security

import com.example.BuildConfig
import java.security.MessageDigest

object AdminAuth {
    internal var overrideHash: String? = null

    /**
     * Verifies the admin PIN using a SHA-256 hash.
     *
     * NOTE: Local PIN verification is only a weak safeguard and should not be
     * relied upon as the sole production admin authorization mechanism.
     * Prefer server-side admin authentication and authorization.
     */
    fun verifyPin(pin: String): Boolean {
        val storedHash = overrideHash ?: BuildConfig.ADMIN_PIN_HASH.takeIf { it.isNotBlank() } ?: return false
        return sha256(pin) == storedHash
    }

    private fun sha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(value.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }
}
