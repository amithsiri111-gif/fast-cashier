package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [UserEntity::class, DepositEntity::class, WithdrawalEntity::class, BankEntity::class],
    version = 2,
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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fastxbet_cashier.db"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
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

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDefaultBanks(database.bankDao())
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
