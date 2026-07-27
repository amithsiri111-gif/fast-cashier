package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BankEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankDao {
    @Query("SELECT * FROM banks WHERE isActive = 1 ORDER BY sortKey ASC")
    fun getActiveBanksFlow(): Flow<List<BankEntity>>

    @Query("SELECT COUNT(*) FROM banks")
    suspend fun getBankCount(): Int

    @Query("DELETE FROM banks")
    suspend fun deleteAllBanks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanks(banks: List<BankEntity>)
}
