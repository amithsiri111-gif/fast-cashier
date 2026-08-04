package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deposits")
data class DepositEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userJid: String = "user_default",
    val playerId: String = "",
    val bankName: String = "",
    val amountText: String = "",
    val amountMinorUnits: Long = 0,
    val slipUri: String? = null,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, CANCELLED
    val reference: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
