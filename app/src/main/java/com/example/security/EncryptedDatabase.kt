package com.example.security

import android.content.Context
import com.example.data.local.AppDatabase

/**
 * This helper is intentionally disabled to avoid introducing an insecure hard-coded
 * database passphrase into the codebase.
 *
 * Database encryption should use a passphrase generated at runtime and stored
 * securely through AndroidX Security (EncryptedSharedPreferences / MasterKey)
 * or a hardware-backed keystore.
 */
object EncryptedDatabase {
    fun create(context: Context): AppDatabase {
        error("EncryptedDatabase.create() is not supported. Use AppDatabase.getDatabase(context) instead.")
    }
}
