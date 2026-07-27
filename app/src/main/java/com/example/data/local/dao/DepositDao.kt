package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DepositEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits ORDER BY createdAt DESC")
    fun getAllDepositsFlow(): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingDepositsFlow(): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits WHERE reference = :ref LIMIT 1")
    suspend fun getDepositByReference(ref: String): DepositEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositEntity): Long

    @Query("UPDATE deposits SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM deposits WHERE status = 'PENDING'")
    fun getPendingCountFlow(): Flow<Int>
}
