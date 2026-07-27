package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banks")
data class BankEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sortKey: Int = 1,
    val bankName: String,
    val accountHolder: String,
    val accountNumber: String,
    val displayNumber: String = "",
    val branch: String = "",
    val isActive: Boolean = true
)
