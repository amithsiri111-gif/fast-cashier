package com.example.data.local

import android.content.Context
import android.util.Base64
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BankDao
import com.example.data.local.dao.DepositDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WithdrawalDao
import com.example.data.local.entity.BankEntity
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom

@Database(
    entities = [UserEntity::class, DepositEntity::class, WithdrawalEntity::class, BankEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun depositDao(): DepositDao
    abstract fun withdrawalDao(): WithdrawalDao
    abstract fun bankDao(): BankDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "fastxbet_cashier.db"
        private const val SECURE_PREFS_NAME = "secure_db_prefs"
        private const val KEY_DB_PASSPHRASE = "db_passphrase"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = getOrCreateDatabasePassphrase(context)
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .openHelperFactory(factory)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun getOrCreateDatabasePassphrase(context: Context): ByteArray {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val securePrefs = EncryptedSharedPreferences.create(
                context,
                SECURE_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val existingPassphrase = securePrefs.getString(KEY_DB_PASSPHRASE, null)
            return if (existingPassphrase != null) {
                Base64.decode(existingPassphrase, Base64.DEFAULT)
            } else {
                val randomBytes = ByteArray(32)
                SecureRandom().nextBytes(randomBytes)
                securePrefs.edit().putString(KEY_DB_PASSPHRASE, Base64.encodeToString(randomBytes, Base64.NO_WRAP)).apply()
                randomBytes
            }
        }

        internal val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // No schema changes in 1 -> 2 for this release path.
            }
        }

        internal val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE deposits ADD COLUMN amountMinorUnits INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE withdrawals ADD COLUMN amountMinorUnits INTEGER NOT NULL DEFAULT 0")
            }
        }

        internal val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_deposits_reference ON deposits(reference)"
                )
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDefaultBanks(database.bankDao())
                        seedDefaultUser(database.userDao())
                    }
                }
            }
        }

        private suspend fun seedDefaultUser(userDao: UserDao) {
            userDao.insertOrUpdate(
                UserEntity(
                    id = "default_user",
                    playerId = "",
                    language = "si"
                )
            )
        }

        private suspend fun seedDefaultBanks(bankDao: BankDao) {
            bankDao.deleteAllBanks()
            val initialBanks = listOf(
                BankEntity(
                    sortKey = 1,
                    bankName = "Bank of Ceylon (BOC)",
                    accountHolder = "Fast Xbet Official",
                    accountNumber = "95645895",
                    displayNumber = "95645895",
                    branch = "Head Office"
                ),
                BankEntity(
                    sortKey = 2,
                    bankName = "People's Bank",
                    accountHolder = "Fast Xbet Official",
                    accountNumber = "120200380030196",
                    displayNumber = "120200380030196",
                    branch = "Main Branch"
                ),
                BankEntity(
                    sortKey = 3,
                    bankName = "Sampath Bank",
                    accountHolder = "Fast Xbet Official",
                    accountNumber = "105456146706",
                    displayNumber = "105456146706",
                    branch = "Main Branch"
                ),
                BankEntity(
                    sortKey = 4,
                    bankName = "LOLC Finance",
                    accountHolder = "Fast Xbet Official",
                    accountNumber = "01210012722",
                    displayNumber = "01210012722",
                    branch = "Head Office"
                ),
                BankEntity(
                    sortKey = 5,
                    bankName = "iPay",
                    accountHolder = "Fast Xbet Official",
                    accountNumber = "0740452530",
                    displayNumber = "0740452530",
                    branch = "iPay Wallet"
                )
            )
            bankDao.insertBanks(initialBanks)
        }
    }
}
