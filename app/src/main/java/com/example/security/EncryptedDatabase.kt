package com.example.security

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.AppDatabase
import com.example.data.local.dao.BankDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.BankEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

object EncryptedDatabase {
    private const val DATABASE_NAME = "fastxbet_cashier_encrypted.db"
    private const val PASSPHRASE = "fastxbet_cashier_secure_passphrase"

    fun create(context: Context): AppDatabase {
        val passphrase = SQLiteDatabase.getBytes(PASSPHRASE.toCharArray())
        val factory = SupportFactory(passphrase)
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .openHelperFactory(factory)
            .addCallback(AppDatabase.DatabaseCallback())
            .build()
    }
}
