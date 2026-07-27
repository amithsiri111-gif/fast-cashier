package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userJid: String = "user_default",
    val playerId: String = "",
    val amountText: String = "",
    val amount: Double = 0.0,
    val secretCode: String = "",
    val bankName: String = "",
    val accountHolder: String = "",
    val accountNumber: String = "",
    val branch: String = "",
    val status: String = "PENDING", // PENDING, APPROVED, COMPLETED, REJECTED, CANCELLED
    val createdAt: Long = System.currentTimeMillis(),
    val payoutReference: String? = null,
    val rejectionReason: String? = null
)
