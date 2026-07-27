package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawals ORDER BY createdAt DESC")
    fun getAllWithdrawalsFlow(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingWithdrawalsFlow(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE status = 'PENDING' LIMIT 1")
    suspend fun getPendingWithdrawal(): WithdrawalEntity?

    @Query("SELECT * FROM withdrawals WHERE id = :id LIMIT 1")
    suspend fun getWithdrawalById(id: Long): WithdrawalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity): Long

    @Query("UPDATE withdrawals SET status = :status, payoutReference = :payoutRef, rejectionReason = :reason WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, payoutRef: String? = null, reason: String? = null)

    @Query("SELECT COUNT(*) FROM withdrawals WHERE status = 'PENDING'")
    fun getPendingCountFlow(): Flow<Int>
}
