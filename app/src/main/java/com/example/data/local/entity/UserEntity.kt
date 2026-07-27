package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String = "default_user",
    val playerId: String = "",
    val language: String = "si", // "si" or "en"
    val savedBankName: String = "",
    val savedAccountHolder: String = "",
    val savedAccountNumber: String = "",
    val savedBranch: String = ""
)
